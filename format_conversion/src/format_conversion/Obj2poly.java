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
public class Obj2poly {
	// this code is used to convert 3-d geological models from OBJ format to POLY format and assign region attributes.
	public static void main(String[] args) {
		// TODO Auto-generated method stub
				//enter the path of the model in the url
				String url=" ";
				String filePath=url;
				String writeFileName="model_polyformat"
						+ ".poly";
				Obj2poly file = new Obj2poly();
				file.readObjFile(filePath);
				file.writePolyFile(writeFileName);
			}
	
	
			//arrays to record vertex
			ArrayList<String> xlist=new ArrayList<>();
			ArrayList<String> ylist=new ArrayList<>();
			ArrayList<String> zlist=new ArrayList<>();
			ArrayList<String> boundary_vtx=new ArrayList<>();
			ArrayList<String> boundary_vtx_o=new ArrayList<>();
			//arrays to record facet
			ArrayList<Integer> p1list=new ArrayList<>();
			ArrayList<Integer> p2list=new ArrayList<>();
			ArrayList<Integer> p3list=new ArrayList<>();		
			ArrayList<String> tflist=new ArrayList<>();
			ArrayList<String> fp=new ArrayList<>();
			ArrayList<String> dellist=new ArrayList<>(); 		
			ArrayList<String> testlist=new ArrayList<>();
			Set<String> hash_testlist = new HashSet<>();
			ArrayList<String> regionlist=new ArrayList<>();		
			Set<String> facetestlist = new HashSet<>();		
			ArrayList<String> vztestlist=new ArrayList<>();
			int counto=0;
			int region_number=0;
			Object[] vx;
			
			public void readObjFile(String filePath) {
				try {
					//start to read data
					File file =new File(filePath);
					if(file.exists()&&file.isFile()) {
						String encoding="utf-8";
						InputStreamReader read =new InputStreamReader(new FileInputStream(file),encoding);
						BufferedReader bufferedReader =new BufferedReader(read);
						String lineText=null;
						//pattern stratum(o)/vertex(v)/facet(f)
						int countv=1;
						int temp=0;
						String patternv="v\\s+(\\-?)\\d*(\\.?)\\d*\\s+(\\-?)\\d*(\\.?)\\d*\\s+(\\-?)\\d*(\\.?)\\d*";
//						String patternf="f\\s+\\d*\\/\\d*\\/\\d*\\s+\\d*\\/\\d*\\/\\d*\\s+\\d*\\/\\d*\\/\\d*";
						String patternf="f\\s+\\d*\\s+\\d*\\s+\\d*";
						String patterno="o.*";
						while((lineText=bufferedReader.readLine())!=null) {
							boolean isv=Pattern.matches(patternv, lineText);
							boolean isf=Pattern.matches(patternf, lineText);
							boolean iso=Pattern.matches(patterno, lineText);
							if(iso) {
								counto++;
								region_number++;
								}
							if(isv) {
								temp++;
								String[] arrv=lineText.split("\\s");
								regionlist.add(arrv[1]+" "+arrv[2]+" "+arrv[3]+" "+String.valueOf(region_number));
								
								if(hash_testlist.contains(arrv[1]+" "+arrv[2]+" "+arrv[3])) {
									int del=testlist.indexOf(arrv[1]+" "+arrv[2]+" "+arrv[3])+1;
									dellist.add(del+"");  								
								}else {
									xlist.addAll(Arrays.asList(arrv[1]));
									ylist.addAll(Arrays.asList(arrv[2]));
									zlist.addAll(Arrays.asList(arrv[3]));
									regionlist.add(String.valueOf(region_number));
									testlist.add(arrv[1]+" "+arrv[2]+" "+arrv[3]);
									hash_testlist.add(arrv[1]+" "+arrv[2]+" "+arrv[3]);
									dellist.add(countv+"");
									countv++;
								}
								arrv=null;
								}
							if(isf) {
								String[] arrf=lineText.split("\\s");
								String vertex1=arrf[1].replaceAll("\\d*\\/\\d*\\/", "");
								String vertex2=arrf[2].replaceAll("\\d*\\/\\d*\\/", "");
								String vertex3=arrf[3].replaceAll("\\d*\\/\\d*\\/", "");
//								p1list.add(Integer.parseInt(vertex1));
//								p2list.add(Integer.parseInt(vertex2));
//								p3list.add(Integer.parseInt(vertex3));
								p1list.add(Integer.parseInt(arrf[1]));
								p2list.add(Integer.parseInt(arrf[2]));
								p3list.add(Integer.parseInt(arrf[3]));
								tflist.add(counto+"");
								arrf=null;
								}
							}
								read.close();			
								System.out.print("Finish reading The OBJ Format model\r\n");
				}else{
					System.out.println("error1");
				}
				}catch(Exception e) {
				System.out.println("error2");
				e.printStackTrace();
				}
			}
			public void writePolyFile(String writeFileName) {
				try {
				//convert lists to arrays before writing
					//vertex related arrays
					int sizev=xlist.size();
					Object[] vx= xlist.toArray(new String[sizev]);
					Object[] vy= ylist.toArray(new String[sizev]);
					Object[] vz= zlist.toArray(new String[sizev]);
					String[] del=(String[])dellist.toArray(new String[sizev]);
					int vxlength=vx.length;
					//facet related arrays
					int sizef=p1list.size();
					System.out.println("sizef"+sizef);
					int[] point1=new int[sizef];
					int[] point2=new int[sizef];								
					int[] point3=new int[sizef];
					String[] numberOfStrata=(String[])tflist.toArray(new String[sizef]);		
					for(int i=0;i<sizef;i++) {
						point1[i]=p1list.get(i);
						point2[i]=p2list.get(i);
						point3[i]=p3list.get(i);
					}
				//use dellist to get the correct and unique index of vertex
					String[] facetVertex1=new String[sizef];
					String[] facetVertex2=new String[sizef];
					String[] facetVertex3=new String[sizef];
					for(int j=0;j<dellist.size();j++) {
						for(int i=0;i<sizef;i++) {
							facetVertex1[i]=dellist.get(point1[i]-1);
							facetVertex2[i]=dellist.get(point2[i]-1);
							facetVertex3[i]=dellist.get(point3[i]-1);
						}
					}
					dellist=null;
				//use faceMark to filter duplicate facets.
				//if the facet i is duplicated, the faceMark[i] will be 0. Otherwise, it will be 1.
					int[] faceMark=new int[sizef];
					String[] regionAttribute=new String[sizef];
					int numf=0;
//					long beginTime = System.currentTimeMillis();	
					for(int i=0;i<sizef;i++) {
						if(facetestlist.contains(facetVertex1[i]+" "+facetVertex2[i]+" "+facetVertex3[i])){
							fp.add(0+" "+0+" "+0);
							regionAttribute[i]=numberOfStrata[fp.indexOf(facetVertex1[i]+" "+facetVertex2[i]+" "+facetVertex3[i])];
							faceMark[i]=0;
							continue;
						}else if(facetestlist.contains(facetVertex2[i]+" "+facetVertex3[i]+" "+facetVertex1[i])){
							fp.add(0+" "+0+" "+0);
							regionAttribute[i]=numberOfStrata[fp.indexOf(facetVertex2[i]+" "+facetVertex3[i]+" "+facetVertex1[i])];
							faceMark[i]=0;
							continue;
						}else if(facetestlist.contains(facetVertex3[i]+" "+facetVertex1[i]+" "+facetVertex2[i])) {
							fp.add(0+" "+0+" "+0);
							regionAttribute[i]=numberOfStrata[fp.indexOf(facetVertex3[i]+" "+facetVertex1[i]+" "+facetVertex2[i])];
							faceMark[i]=0;
							continue;
						}else if(facetestlist.contains(facetVertex1[i]+" "+facetVertex3[i]+" "+facetVertex2[i])) {
							fp.add(0+" "+0+" "+0);
							regionAttribute[i]=numberOfStrata[fp.indexOf(facetVertex1[i]+" "+facetVertex3[i]+" "+facetVertex2[i])];
							faceMark[i]=0;
							continue;
						}else if(facetestlist.contains(facetVertex2[i]+" "+facetVertex1[i]+" "+facetVertex3[i])) {
							fp.add(0+" "+0+" "+0);
							regionAttribute[i]=numberOfStrata[fp.indexOf(facetVertex2[i]+" "+facetVertex1[i]+" "+facetVertex3[i])];
							faceMark[i]=0;
							continue;
						}else if(facetestlist.contains(facetVertex3[i]+" "+facetVertex2[i]+" "+facetVertex1[i])) {
							fp.add(0+" "+0+" "+0);
							regionAttribute[i]=numberOfStrata[fp.indexOf(facetVertex3[i]+" "+facetVertex2[i]+" "+facetVertex1[i])];
							faceMark[i]=0;
							continue;
						}
						else {
							facetestlist.add(facetVertex1[i]+" "+facetVertex2[i]+" "+facetVertex3[i]);
							fp.add(facetVertex1[i]+" "+facetVertex2[i]+" "+facetVertex3[i]);
							regionAttribute[i]=numberOfStrata[i];
							faceMark[i]=1;
							numf++;
						}
					}
					for(int i=0;i<sizef;i++) {
						if(faceMark[i]==0) {		
						}
					}
					//find seed points and assign region attributes
					//vertices on the boundary of stratum are stored in boundary_vtx and the corresponding stratum markers are stored in boundary_vtx_o.
					int[] num_vtx_o=new int[vxlength];
					for(int i=0;i<num_vtx_o.length;i++) {
						num_vtx_o[i]=0;
					}
					//record boundary vertices
					for(int i=0;i<sizef;i++) {
					//faceMark==0 ¡ú duplicated facet
						if(faceMark[i]==1) {
							continue;
						}
						if(num_vtx_o[Integer.parseInt(facetVertex1[i])-1]!=0&&num_vtx_o[Integer.parseInt(facetVertex1[i])-1]!=Integer.parseInt(regionAttribute[i])) {
							boundary_vtx.add(facetVertex1[i]);
							boundary_vtx.add(facetVertex2[i]);
							boundary_vtx.add(facetVertex3[i]);
							boundary_vtx_o.add(num_vtx_o[Integer.parseInt(facetVertex1[i])-1]+"");
						}else if(num_vtx_o[Integer.parseInt(facetVertex1[i])-1]==0){
							num_vtx_o[Integer.parseInt(facetVertex1[i])-1]=Integer.parseInt(regionAttribute[i]);
						}
						
						if(num_vtx_o[Integer.parseInt(facetVertex2[i])-1]!=0&&num_vtx_o[Integer.parseInt(facetVertex2[i])-1]!=Integer.parseInt(regionAttribute[i])) {
							boundary_vtx.add(facetVertex1[i]);
							boundary_vtx.add(facetVertex2[i]);
							boundary_vtx.add(facetVertex3[i]);
							boundary_vtx_o.add(num_vtx_o[Integer.parseInt(facetVertex2[i])-1]+"");
						}else if(num_vtx_o[Integer.parseInt(facetVertex2[i])-1]==0){
							num_vtx_o[Integer.parseInt(facetVertex2[i])-1]=Integer.parseInt(regionAttribute[i]);
						}
						
						if(num_vtx_o[Integer.parseInt(facetVertex3[i])-1]!=0&&num_vtx_o[Integer.parseInt(facetVertex3[i])-1]!=Integer.parseInt(regionAttribute[i])) {
							boundary_vtx.add(facetVertex1[i]);
							boundary_vtx.add(facetVertex2[i]);
							boundary_vtx.add(facetVertex3[i]);
							boundary_vtx_o.add(num_vtx_o[Integer.parseInt(facetVertex3[i])-1]+"");
						}else if(num_vtx_o[Integer.parseInt(facetVertex3[i])-1]==0){
							num_vtx_o[Integer.parseInt(facetVertex3[i])-1]=Integer.parseInt(regionAttribute[i]);
						}
					}
				// calculate the displacement limit on Z-direction of each stratum
					double[] z_limit=new double[13];
					double[] zmin=new double[13];
					for(int i=1;i<13;i++) {
						double max=-100;
						double min=100;
						for(int j=0;j<sizef;j++) {
						if(Integer.parseInt(regionAttribute[j])==i&&faceMark[j]==0) {
							double z1=Double.parseDouble(vz[Integer.parseInt(facetVertex1[j])-1].toString());
							double z2=Double.parseDouble(vz[Integer.parseInt(facetVertex2[j])-1].toString());
							double z3=Double.parseDouble(vz[Integer.parseInt(facetVertex3[j])-1].toString());
							double temp1=Math.max(z1, z2);
							double temp2=Math.max(temp1, z3);
							if(temp2>max) {
								max=temp2;
								}
							z_limit[i]=max;
							}
						}
					}
					Set<String> hs_b_v=new HashSet<>(boundary_vtx);
					ArrayList<String> attribute=new ArrayList<>();
					//move the remaining vertices by a short distance(not exceed the limit) as seed points
					for(int i=0;i<sizef;i++) {
						if(faceMark[i]==1) {
							continue;
						}
						else if(hs_b_v.contains(facetVertex1[i])||hs_b_v.contains(facetVertex2[i])||hs_b_v.contains(facetVertex3[i])) {
							continue; 
						}else {
						double z1=Double.parseDouble(vz[Integer.parseInt(facetVertex1[i])-1].toString())+0.000001;
						double z2=Double.parseDouble(vz[Integer.parseInt(facetVertex2[i])-1].toString())+0.000001;
						double z3=Double.parseDouble(vz[Integer.parseInt(facetVertex3[i])-1].toString())+0.000001;

						if(z_limit[Integer.parseInt(regionAttribute[i])]>z1&&z_limit[Integer.parseInt(regionAttribute[i])]>z2&&z_limit[Integer.parseInt(regionAttribute[i])]>z3) {
						attribute.add(vx[Integer.parseInt(facetVertex1[i])-1].toString()+" "+vy[Integer.parseInt(facetVertex1[i])-1].toString()+" "+z1+" "+regionAttribute[i]);
						attribute.add(vx[Integer.parseInt(facetVertex2[i])-1].toString()+" "+vy[Integer.parseInt(facetVertex2[i])-1].toString()+" "+z2+" "+regionAttribute[i]);
						attribute.add(vx[Integer.parseInt(facetVertex3[i])-1].toString()+" "+vy[Integer.parseInt(facetVertex3[i])-1].toString()+" "+z3+" "+regionAttribute[i]);					
								}
							}
						}
//					long endTime = System.currentTimeMillis();
//					System.out.println("cost time: " + (endTime - beginTime) + "ms");	
				
				//start to write
					File file1=new File(writeFileName);
					FileWriter fw=new FileWriter(writeFileName);
					//write vertex
					fw.write(vx.length+" "+"3 0 0");
					fw.write("\r\n");
					for(int i=0;i<vx.length;i++) {
						int number=i+1;
						fw.write(number+" "+vx[i]+" "+vy[i]+" "+vz[i]);
						fw.write("\r\n");
					}
					//write facet
					if(counto!=0) {
					fw.write(numf+" 1");
					fw.write("\r\n");
					for(int i=0;i<sizef;i++) {
						if(faceMark[i]==0) {
							continue;
						}
								fw.write("1 0 "+numberOfStrata[i]);
								fw.write("\r\n");
								fw.write("3"+" "+facetVertex1[i]+" "+facetVertex2[i]+" "+facetVertex3[i]);
								fw.write("\r\n");
							}
					}else {
					fw.write(numf+" 0");
					fw.write("\r\n");
					for(int i=0;i<sizef;i++) {
						if(faceMark[i]==0) {
							continue;
						}
								fw.write("1 0 "+numberOfStrata[i]);
								fw.write("\r\n");
								fw.write("3"+" "+facetVertex1[i]+" "+facetVertex2[i]+" "+facetVertex3[i]);
								fw.write("\r\n");
							}
					}
					//write hole
					fw.write("0");
					fw.write("\r\n");
					//write region attribute
					fw.write(attribute.size()+"");
					fw.write("\r\n");
					for(int i=0;i<attribute.size();i++) {
						int k=i+1;
						fw.write(k+" "+attribute.get(i));
						fw.write("\r\n");
					}
					//finish writing
					fw.close();
				}catch(Exception e) {
					System.out.println("error3");
					e.printStackTrace();
				}

			}

}
			


