package org.ieselcaminas.alu53787365w.exam2017;

import android.util.Log;
import android.widget.Button;

public class HiloTac extends Thread{
	void text(Button b){
		for(int i=0; i<=10000; i++){
			b.setText("TIC");
			b.setText("TAC");
		}

	}
	public void run(){
		try{
			while(true){

				Log.d("TAC","TAC");

				Thread.sleep(1000);
			}
		}catch(InterruptedException e){
			System.out.println("Error tipo " + e);
		}
		
		
	}
}