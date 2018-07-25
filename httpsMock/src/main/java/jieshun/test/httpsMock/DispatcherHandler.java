package jieshun.test.httpsMock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

//import org.apache.log4j.Logger;
import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

public class DispatcherHandler {

	protected static Logger logger  = LoggerFactory.getLogger(DispatcherHandler.class);
	private static String basePath = System.getProperty("user.dir");
			//(String) ClientMain.props.get("path");
			
	protected static Map<String,String> mapCache = new HashMap<String,String>();
	
	public DispatcherHandler(){
		initCache(basePath + File.separator+"protocol");
	}
	
	public static Map<String,String> refreshChache(){
		mapCache.clear();
		initCache(basePath + File.separator+"protocol");
		
		return mapCache;
	}
	
	private static void initCache(String path) {
		// TODO Auto-generated method stub
		File file = new File(path);
		if(file.exists()){
			File[] files = file.listFiles();
			if(files.length == 0){
				logger.info("文件夹为空。");
				//System.out.println("文件夹是空的");
			}else{
				for(File tempFile:files){
					if(tempFile.isDirectory()){
						logger.info("文件夹:{}",tempFile.getAbsolutePath());
						//增加递归调用
						initCache(tempFile.getAbsolutePath());
						System.out.println("文件夹：" + tempFile.getAbsolutePath());
					}else if(tempFile.getName().endsWith(".txt")){
						logger.info("txt文件:{}",tempFile.getAbsolutePath());
						System.out.println("txt文件:" + tempFile.getAbsolutePath());
						//    <key,value>
						//例如：<NISSP_JSPAY_ORDER,参数内容>
						mapCache.put(tempFile.getName().replace(".txt", ""), 
								readTxtFile(tempFile.getAbsolutePath()));
					}else{
						logger.info("---------文件格式不正确！");
						
					}
				}
			}
		}else{
			logger.error("-----------文件不存在。");
		}
		
	}


	public static String selectProtocol(String uri){
		String result = "";
		
		if(mapCache.get(uri) != null){
			
			logger.info("----------------消息取自缓存！");
			result = mapCache.get(uri);//如果缓存中有相应记录，那么直接从缓存中取值；否则的话，就在重新从对应txt文件中读取出来。
		}else{
			logger.info("=======消息实时读取。");
			result = readTxtFile(basePath + File.separator+"protocol"+File.separator + uri + ".txt");
		}
		
		return "".equals(result)?"根据"+uri.replace("_", "/")+"未匹配到对应的返回值！":result;
	}


	protected static String readTxtFile(String path) {
		// TODO Auto-generated method stub
		StringBuilder result = new StringBuilder();
		
		try{
			File file = new File(path);
			if(file.isFile() && file.exists()){
				InputStreamReader isr = new InputStreamReader(new FileInputStream(file),"utf-8");
				BufferedReader br = new BufferedReader(isr);
				String lineTxt = null ;
				while((lineTxt = br.readLine()) != null){
					result.append(lineTxt);
				}
				br.close();
			}else{
				logger.info("文件不存在。");
			}
		}catch(Exception e){
			logger.error("文件读取错误。");
		}
		return result.toString();
	}
	
	
}
