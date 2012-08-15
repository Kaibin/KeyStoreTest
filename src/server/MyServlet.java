package server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.ArrayUtils;

import util.CertificateCoder;

public class MyServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6197211740957851460L;
	private static String alias = "7aa1ca0cc9745b09a77a65f6199c05d7_c4da2f84-863b-4f57-af4d-09e7b9808475";
	private static String keyStorePath = System.getProperty("user.dir")+ System.getProperty("file.separator")+ "conf"+ System.getProperty("file.separator") + "cert.keystore";
	private static String password = "123456";
	private static String prikeyPath = System.getProperty("user.dir")+ System.getProperty("file.separator")+ "conf"+ System.getProperty("file.separator") + "prikey.dat";


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		System.out.println("doGet");
		resp.getWriter().write("hello");
		resp.getWriter().close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("doPost");
		
		//for test
		InputStream is = null;
		try{
			is = req.getInputStream();
			byte[] startByte = new byte[8];
			is.read(startByte);
			String startFlag = new String(startByte,"UTF-8");
			System.out.println(startFlag);
		}catch (Exception e) {
			if (is != null) { 
				is.close(); 
            } 
		}
		
		
		OutputStream os = resp.getOutputStream();
		
		//对DES对称密钥用RSA算法加密
		byte[] encodedByteArray = encryptPrivateKey();
		
		resp.setContentType("text/xml");
        
		resp.setContentLength(encodedByteArray.length);
        
        //发送密钥
		os.write(encodedByteArray);
		os.flush();
		os.close();
		
		
		
		
		
	}
	
	//对DES对称密钥用RSA算法加密
	private byte[] encryptPrivateKey(){
		
		byte[] bytes = Stream2Byte(prikeyPath);
		
		byte[] encodedByteArray = new byte[0];
		
		for (int i = 0; i < bytes.length; i += 102) {
			byte[] subarray = ArrayUtils.subarray(bytes, i, i + 102 + 1);
			byte[] encrypt;
			try {
				encrypt = CertificateCoder.encryptByPrivateKey(subarray, keyStorePath, alias, password);
				encodedByteArray = ArrayUtils.addAll(encodedByteArray,encrypt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return encodedByteArray;
	}
	
	
	public byte[] Stream2Byte(String infile) {
		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(infile));
			out = new ByteArrayOutputStream(1024);

			System.out.println("Available bytes:" + in.available());

			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = in.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		byte[] content = out.toByteArray();
		System.out.println("Readed bytes count:" + content.length);
		return content;
    }

}

