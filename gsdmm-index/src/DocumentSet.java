import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;

public class DocumentSet{
	int D = 0;
	ArrayList<Document> documents = new ArrayList<Document>();
	
	public DocumentSet(String dataDir, HashMap<String, Integer> wordToIdMap) 
			 					throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(dataDir));
		String line;
		int wordNum = 0;
		int length = 0;
		while((line=in.readLine()) != null){
			D++;
			JSONObject obj = new JSONObject(line);
			String text = obj.getString("text");
			Document document = new Document(text, wordToIdMap);
			documents.add(document);
			wordNum += document.wordNum;
			length += document.num;
		}
		System.out.println("average l: " + length/D);
		System.out.println("size: " + D);
		System.out.println("average v: " + wordNum/D);
		System.out.println("V: " + wordToIdMap.size());
		in.close();
	}
}
