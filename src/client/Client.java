package client;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang.ArrayUtils;

import util.CertificateCoder;

public class Client {
	
	public static final int SIZE_INPUT_BUFFER = 4096;
	public static String certificatePath = System.getProperty("user.dir")+ System.getProperty("file.separator")+ "conf"+ System.getProperty("file.separator") + "cert.cer";
	private static String prikeyPath2 = System.getProperty("user.dir")+"/conf/prikey2.dat";

	
	public static void main(String[] args ){
		try{
			URL url = new URL("http://127.0.0.1:6000/remote");
			HttpURLConnection  conn = (HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			
			//发送握手标志
			OutputStream os = conn.getOutputStream();
			String startFlag = "11110000";
			byte[] startByte = startFlag.getBytes("UTF-8");
			os.write(startByte);
			os.flush();
			os.close();
			
			
			
			InputStream is = conn.getInputStream();
			byte[] requestData = readContent(is);
			
			// 解密密钥
			byte[] decodedByteArray = decryptPrikey(requestData);
			
			//保存密钥文件
			savePrikey(decodedByteArray);

		}catch (Exception e) {
			e.printStackTrace();
		}
		

	}
	
	private static void savePrikey(byte[] decodedByteArray){
		FileOutputStream fw = null;
		try {
			fw = new FileOutputStream(prikeyPath2);
			fw.write(decodedByteArray);
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(fw != null){
					fw.close();
					fw = null;
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static byte[] decryptPrikey(byte[] keyData){
		byte[] decodedByteArray = new byte[0];
		for (int i = 0; i < keyData.length; i += 128) {

			byte[] subarray = ArrayUtils.subarray(keyData, i, i + 128);
			byte[] decrypt;
			try {
				decrypt = CertificateCoder.decryptByPublicKey(subarray,
						certificatePath);
				decodedByteArray = ArrayUtils.addAll(decodedByteArray, decrypt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return decodedByteArray;
	}
	
	protected static byte[] readContent(final InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buf = new byte[SIZE_INPUT_BUFFER];

		int c = 0;
		int b = 0;
		while ((c < buf.length) && (b = in.read(buf, c, buf.length - c)) >= 0) {
			c += b;
			if (c == SIZE_INPUT_BUFFER) {
				bout.write(buf);
				buf = new byte[SIZE_INPUT_BUFFER];
				c = 0;
			}
		}
		if (c != 0) {
			bout.write(buf, 0, c);
		}
		return bout.toByteArray();
	}
	
}
