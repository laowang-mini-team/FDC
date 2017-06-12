""" parse dataset """
import glob
import os
import os.path
import json
import csv
import string
import re

import nltk
from nltk.tokenize import word_tokenize
from nltk.corpus import wordnet
from nltk.stem import WordNetLemmatizer


DATA_DIR = './data/'
STOPWORDS = set(nltk.corpus.stopwords.words('english'))
PUNCTUATIONS = string.punctuation
WNL = WordNetLemmatizer()


def _word_segmentation(text):
    text = re.sub('[%s]' % PUNCTUATIONS, '', text)           # remove punctuations
    text = [word.lower() for word in word_tokenize(text)]    # split
    text = [word for word in text if word not in STOPWORDS]  # remove stopwords
    # stem
    to_stem_text = text
    text = list()
    for word in to_stem_text:
        stemmed_word = wordnet.morphy(word)
        if stemmed_word is None:
            stemmed_word = WNL.lemmatize(word)
        text.append(stemmed_word)
    return ' '.join(text)


def _get_line(content, cluster_index):
    return {
        'text': _word_segmentation(content),
        'cluster': cluster_index
    }


def _parse_standard_dataset(dataset, base_path, file_type):
    """ parse dataset which has categories and documents in them """
    categories = [path for path in os.listdir(base_path) if os.path.isdir(base_path + path)]

    result = open(DATA_DIR + dataset, 'w', encoding='utf-8')
    for (cluster_index, cluster_name) in enumerate(categories):
        path = base_path + cluster_name
        data_files = glob.glob(path + '/' + file_type)
        for file_path in data_files:
            with open(file_path, encoding='latin-1') as document:
                content = document.read().encode().decode('utf-8')
                content = json.dumps(_get_line(content, cluster_index))
                result.write(content + '\n')

    result.close()

def parse_bbc():
    """ bbc """
    print('parsing bbc dataset...')
    dataset = 'bbc'
    base_path = './bbc-fulltext/bbc/'

    _parse_standard_dataset(dataset, base_path, '*.txt')


def parse_ohsumed():
    """ ohsumed """
    print('parsing ohsumed dataset...')
    dataset = 'ohsumed'
    base_path = './ohsumed-all-docs/ohsumed-all/'

    _parse_standard_dataset(dataset, base_path, '*')


def parse_qa():
    """ question answer """
    print('parsing Question_Answer dataset...')
    dataset = 'Question_Answer'
    base_path = './Question_Answer_Dataset_v1.2/'

    ss = ['S08', 'S09', 'S10']
    categories = dict()

    result = open(DATA_DIR + dataset, 'w', encoding='utf-8')
    for s in ss:
        category_path = base_path + s + '/data/'
        s_categories = [path for path in os.listdir(category_path) if os.path.isdir(category_path + path)]
        for cluster_name in s_categories:
            cluster_index = categories.get(cluster_name, None)
            if cluster_index is None:
                cluster_index = len(categories) + 1
                categories[cluster_name] = cluster_index
            path = category_path + cluster_name
            data_files = glob.glob(path + '/*.txt.clean')
            for file_path in data_files:
                with open(file_path, encoding='latin-1') as document:
                    content = document.read().encode().decode('utf-8')
                    content = json.dumps(_get_line(content, cluster_index))
                    result.write(content + '\n')

    result.close()


def parse_question():
    """  question data """
    print('parsing question_data dataset...')
    dataset = 'question_data'
    base_path = './question_data/'

    categories = dict()
    title_category_map = dict()

    questions_path = base_path + 'questions.csv'
    with open(questions_path, encoding='utf-8') as questions_file:
        reader = csv.DictReader(questions_file)
        next(reader)
        for row in reader:
            title = row['Answer']
            category = row['Category']
            category_index = categories.get(category, None)
            if category_index is None:
                categories[category] = len(categories) + 1
            title_category_map[title] = category

    result = open(DATA_DIR + dataset, 'w', encoding='utf-8')
    for title, cluster_name in title_category_map.items():
        cluster_index = categories[cluster_name]
        file_path = base_path + 'wiki/' + title + '.txt'
        if os.path.isfile(file_path):
            with open(file_path, encoding='latin-1') as document:
                content = document.read().encode().decode('utf-8')
                content = json.dumps(_get_line(content, cluster_index))
                result.write(content + '\n')

    result.close()


def parse_reuters21578():
    """ Reuters21578 """
    print('parsing Reuters21578 dataset...')
    dataset = 'Reuters21578'
    base_path = './Reuters21578-Apte-115Cat/training/'

    _parse_standard_dataset(dataset, base_path, '*')


if __name__ == '__main__':
    parse_bbc()
    parse_ohsumed()
    parse_qa()
    parse_question()
    parse_reuters21578()
