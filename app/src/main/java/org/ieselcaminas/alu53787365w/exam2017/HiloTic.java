package org.ieselcaminas.alu53787365w.exam2017;

import android.util.Log;
import android.widget.Button;

public class HiloTic extends Thread{
	void text(Button b){
		b.setText("TIC");
	}
	public void run(){
		try{
			Thread.sleep(500);
			while(true){
				Log.d("TIC","TIC");
				Thread.sleep(1000);
			}
		}catch(InterruptedException e){
			System.out.println("Error tipo " + e);
		}
		
		
	}
}