package com.example.joel.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class guess extends AppCompatActivity {
    ArrayList<String> celebUrls=new ArrayList<>();
    ArrayList<String> celebNames=new ArrayList<>();
    int chosenCeleb=0;
    int locationCorrect=0;
    String ans[]=new String[4];
    ImageView imageView;
    Button button0,button1,button2,button3;
    public void CelebChosen(View view)
    {
        if(view.getTag().toString().equals(Integer.toString(locationCorrect)))
        {
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Wrong! It was "+celebNames.get(chosenCeleb),Toast.LENGTH_SHORT).show();
        }
        generateQuestion();
    }
    public  class  ImageDownloader extends  AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in=connection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                Bitmap bitmap= BitmapFactory.decodeStream(in);
                return bitmap;


            } catch (Exception e)

            {
            }
            return  null;
        }
    }
    public class DownloadTask extends AsyncTask<String,Void, String>
    {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection connection=null;
            try{
                url=new URL(urls[0]);
                connection=(HttpURLConnection)url.openConnection();
                InputStream in=connection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1)
                {
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);
        imageView=(ImageView)findViewById(R.id.imageView2);
        button0=(Button)findViewById(R.id.button1);
        button1=(Button)findViewById(R.id.button2);
        button2=(Button)findViewById(R.id.button3);
        button3=(Button)findViewById(R.id.button4);
        DownloadTask task=new DownloadTask();
        String result="";
        try {
            result=task.execute("http://www.posh24.se/kandisar").get();
            String splitResult[]=result.split("<div class=\"sidebarContainer\">");
            Pattern pattern=Pattern.compile("<img src=\"(.*?)\"");
            Matcher matcher=pattern.matcher(splitResult[0]);
            while (matcher.find())
            {
                celebUrls.add(matcher.group(1));
            }
            pattern=Pattern.compile("alt=\"(.*?)\"");
            matcher=pattern.matcher(splitResult[0]);
            while (matcher.find())
            {
                celebNames.add(matcher.group(1));
            }
            generateQuestion();

        }catch (Exception e)
        {

        }

    }
    public void generateQuestion()
    {
        Random random=new Random();
        chosenCeleb=random.nextInt(celebUrls.size());
        locationCorrect=random.nextInt(4);
        int incorrect=0;
        for(int i=0;i<4;i++)
        {
            if(i==locationCorrect)
            {
                ans[i]=celebNames.get(chosenCeleb);
            }
            else
            {
                incorrect=random.nextInt(celebNames.size());
                while (incorrect==chosenCeleb)
                {
                    incorrect=random.nextInt(celebNames.size());
                }
                ans[i]=celebNames.get(incorrect);
            }

            button0.setText(ans[0]);
            button1.setText(ans[1]);
            button2.setText(ans[2]);
            button3.setText(ans[3]);
        }
        ImageDownloader imageTask=new ImageDownloader();
        Bitmap image= null;
        try {
            image = imageTask.execute(celebUrls.get(chosenCeleb)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(image);

    }
}
