package format_conversion;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.*;

import format_conversion.Node_ele2f3grid;

public class Node_ele2f3grid{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//enter the path of the .node file in url1
		//enter the path of the .ele file in url2
		String url1="";
		String url2="";
		String nodePath=url1;
		String elePath=url2;
		String writeFileName="model_f3grid.f3grid";
		Node_ele2f3grid file = new Node_ele2f3grid();
		file.Tetgen2Flac3d(nodePath,elePath,writeFileName);
	}
	
	ArrayList<String> nodelist=new ArrayList<>();
	ArrayList<String> elelist=new ArrayList<>();
	
	public void Tetgen2Flac3d(String nodePath,String elePath, String writeFileName) {
		try {
			//start to read data
			
			//read vertices
			File node =new File(nodePath);
			if(node.exists()&&node.isFile()) {
				String encoding="utf-8";
				InputStreamReader read =new InputStreamReader(new FileInputStream(node),encoding);
				BufferedReader bufferedReader =new BufferedReader(read);
				String lineText=null;
				//read index and coordinate of vertex
				//int float float float
				String patternnode="\\s*\\d*\\s*+(\\-?)\\d*(\\.?)\\d*\\s*+(\\-?)\\d*(\\.?)\\d*\\s*+(\\-?)\\d*(\\.?)\\d*";
				while((lineText=bufferedReader.readLine())!=null) {
					boolean isnode=Pattern.matches(patternnode,lineText);
					String[] arraynode=lineText.split("\\s");
					boolean notindex=Pattern.matches("\\s*\\d*\\s*+\\d*\\s*+\\d*\\s*+\\d*", lineText);
					if(isnode&&!notindex) {
						nodelist.add(lineText);
					}
				}
			}else {
				System.out.print("node file is not exist");
			}
			//read elements
			File ele =new File(elePath); 
			if(ele.exists()&&ele.isFile()) {
				String encoding="utf-8";
				InputStreamReader read =new InputStreamReader(new FileInputStream(ele),encoding);
				BufferedReader bufferedReader =new BufferedReader(read);
				String lineText=null;
				//read index and coordinate of element
				//int int int int int int
				String patternele="\\s*(\\d+)(\\s+)+(\\d+)(\\s+)+(\\d+)(\\s+)+(\\d+)(\\s+)+(\\d+)(\\s+)+(\\d+)";
				while((lineText=bufferedReader.readLine())!=null) {
					boolean isele=Pattern.matches(patternele,lineText);
					if(isele) {
						String[] arrayele=lineText.trim().split("(\\s+)");
						elelist.add(arrayele[0]+" "+arrayele[1]+" "+arrayele[2]+" "+arrayele[3]+" "+arrayele[4]);
					}
				}
			}else {
				System.out.print("ele file is not exist");
			}
			
			//start to write file
			File file=new File(writeFileName);
			FileWriter fw=new FileWriter(writeFileName);
			fw.write("*Vertex");
			fw.write("\r\n");
			for(int i=0;i<nodelist.size();i++) {
				fw.write("G"+" "+nodelist.get(i));
				fw.write("\r\n");
			}
			fw.write("*Zones");
			fw.write("\r\n");
			for(int i=0;i<elelist.size();i++) {
				fw.write("Z"+" "+"T4"+" "+elelist.get(i));
				fw.write("\r\n");
			}
				fw.close();
		}catch(Exception e) {
			System.out.println("error2");
			e.printStackTrace();
		}
	}
}
