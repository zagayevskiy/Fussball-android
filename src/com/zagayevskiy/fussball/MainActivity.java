package com.zagayevskiy.fussball;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;

import com.zagayevskiy.fussball.utils.C;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

public class MainActivity extends Activity {

	private static BigInteger g512 = new BigInteger("1234567890", 16);

	private static BigInteger p512 = new BigInteger("1234567890", 16);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		
		if("".equals(prefs.getString(C.prefs.key.ACCESS_TOKEN, ""))){
			startActivity(new Intent(this, AuthActivity.class));
			finish();
		}
		
//		try {
//			
//			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
//			keyGen.initialize(new DHParameterSpec(p512, g512, 512));
//
//			KeyAgreement aKeyAgree = KeyAgreement.getInstance("DH", "BC");
//			KeyPair aPair = keyGen.generateKeyPair();
//			KeyAgreement bKeyAgree = KeyAgreement.getInstance("DH", "BC");
//			KeyPair bPair = keyGen.generateKeyPair();
//
//			aKeyAgree.init(aPair.getPrivate());
//			bKeyAgree.init(bPair.getPrivate());
//
//			aKeyAgree.doPhase(bPair.getPublic(), true);
//			bKeyAgree.doPhase(aPair.getPublic(), true);
//
//			MessageDigest hash = MessageDigest.getInstance("SHA1");
//			
//			Log.i("A", Base64.encodeToString(hash.digest(aKeyAgree.generateSecret()), Base64.DEFAULT));
//			Log.i("B", Base64.encodeToString(hash.digest(bKeyAgree.generateSecret()), Base64.DEFAULT));
//		} catch (GeneralSecurityException e) {
//			Log.e("Security", "Exception", e);
//		}

	}
}
