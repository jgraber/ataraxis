package ataraxis.crypt;

import java.io.IOException;




public class Base64Tests {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException 
	{
	
		sun.misc.BASE64Encoder suns = new sun.misc.BASE64Encoder();
		sun.misc.BASE64Decoder sunsd = new sun.misc.BASE64Decoder();
		org.bouncycastle.util.encoders.Base64 bcs = new org.bouncycastle.util.encoders.Base64();
		String one = "1. Dies hätte einige üöäüäüä";
		String two = "2. Keinerlei Umlaute";
		String sunOne = suns.encode(one.getBytes());
		String sunTwo = suns.encode(two.getBytes());
		String bcOne = new String(bcs.encode(one.getBytes()));
		String bcTwo = new String(bcs.encode(two.getBytes()));
		System.out.println(sunOne);
		System.out.println(bcOne);
		System.out.println(sunTwo);
		System.out.println(bcTwo);
		System.out.println("-----");
		System.out.println(new String(sunsd.decodeBuffer(sunOne)));
		System.out.println(new String(bcs.decode(bcOne)));
		System.out.println(new String(sunsd.decodeBuffer(sunTwo)));
		System.out.println(new String(bcs.decode(bcTwo)));
		System.out.println(new String(sunsd.decodeBuffer(bcOne)));
		System.out.println(new String(bcs.decode(sunOne)));
	}

}
