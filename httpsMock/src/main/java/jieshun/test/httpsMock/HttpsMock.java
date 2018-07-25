package jieshun.test.httpsMock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

@RestController
@EnableAutoConfiguration
public class HttpsMock {
	protected static Logger logger  = LoggerFactory.getLogger(HttpsMock.class);
	protected static String baseUrl="http://127.0.0.1:8080";
	private static long requestCount=0;

	


	public static String getBaseUrl() {
		return baseUrl;
	}

	public static void setBaseUrl(String baseUrl) {
		HttpsMock.baseUrl = baseUrl;
	}
	//检测接口
	@RequestMapping("/")
	String home(HttpServletRequest req) {
		
		String result="<h5>Spring Boot Testing! <br/>" + ">>欢迎访问httpMock系统，您的IP为:" + req.getRemoteHost() + "</h5><p><a href=/queryMethodList>进入mock列表</a></p>";
		
		return result;
	}
	//查询接口
	@RequestMapping("/queryMethodList")
	String getMethodList() {
		//请求次数归零
		requestCount=0;
		StringBuffer result = new StringBuffer("<ul>");
		
		String [] methodList=DispatcherHandler.refreshChache()
				.keySet().toString().replace("_", "/").replace("[", "").replace("]", "").split(",");
		for(String str:methodList){
			result.append("<li><a href="+str.replace(" ", "")+">").append(str).append("</a></li>");
		}
		result.append("</ul>");
		return result.toString();
	}
	//新增接口
	@RequestMapping("/addMethod")
	String getMethodList1(ModelMap map) {
	
		
		String [] methodList=DispatcherHandler.mapCache.keySet().toString().replace("_", "/").replace("[", "").replace("]", "").split(",");
		for(String str:methodList){
			
			map.addAttribute("methodList",baseUrl+str.replace(" ", ""));
		}
	
		return "addMethod";
	}

	//接口mock程序
	@RequestMapping("/**")
	String httpsMock(HttpServletRequest req, HttpServletResponse res) throws UnsupportedEncodingException {
		//统计请求次数，防止线程安全，加同步锁
		synchronized(this)
			{
				requestCount++;
				
			}
		
		try {
			req.setCharacterEncoding("utf-8");
			res.setCharacterEncoding("utf-8");

			BufferedReader reader = req.getReader();
			String input=null;
			StringBuffer sb = new StringBuffer();
			while((input=reader.readLine())!=null){
				sb.append(input);
			}
			
			
			logger.debug("接收到的请求的IP："+req.getRemoteHost()+" 请求内容为：-----"+JSON.toJSONString(req.getParameterMap())+sb.toString()+"---请求次数："+requestCount);
			System.out.println("["+new Date()+"]"+"  接收到的请求的IP："+req.getRemoteHost()+" 请求内容为：----->>"+JSON.toJSONString(req.getParameterMap())+sb.toString()+"--->>请求次数："+requestCount);
			
			
			String result=DispatcherHandler.
					selectProtocol(req.getRequestURI().replace("/", "_"));

			return result;
			
		} catch(IOException e1){
			e1.printStackTrace();
		}
		return "返回值未设置~！";
		
		
	}
	//更新接口
	@RequestMapping("/updateMethod")
	String updateResult(HttpServletRequest req) {
		logger.info("------------------"+req.getRequestURI());
		return "更新：<input type='text' name='test' /><input type='button' name='test1' value='测试'/>";
	}
	
//	public static void main(String[] args) {
//		try {
//			System.out.println(InetAddress.getLocalHost().getHostAddress());
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}