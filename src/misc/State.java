package misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

public abstract class State implements Cloneable {
    protected ArrayList<String> listJoke        = new ArrayList<String>();
    protected boolean           jokeListTainted = false;

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public synchronized void readJokesFile(String filePath) {
        FileInputStream fileInputStream = null;
        InputStreamReader inputStremReader = null;
        BufferedReader bufferedReader = null;

        try {
            // creates the file if it doesn't exist
            new FileOutputStream(filePath, true).close();
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

        try {
            fileInputStream = new FileInputStream(filePath);
            inputStremReader = new InputStreamReader(fileInputStream, Charset.defaultCharset());
            bufferedReader = new BufferedReader(inputStremReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line;

        try {
            if (bufferedReader.ready()) {
                while ((line = bufferedReader.readLine()) != null) {
                    listJoke.add(line);
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (inputStremReader != null) {
            try {
                inputStremReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void saveJokeList(String filePath) {
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;

        try {
            fileOutputStream = new FileOutputStream(filePath);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, Charset.defaultCharset());
            bufferedWriter = new BufferedWriter(outputStreamWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        for (String joke : listJoke) {
            try {
                bufferedWriter.write(joke);
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bufferedWriter.close();
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.jokeListTainted = false;

    }

    public synchronized void saveJokeListIfNeed(String filepath) {
        if (this.jokeListTainted) {
            this.saveJokeList(filepath);
        }
    }

    public synchronized boolean addJoke(String newJoke) {
        boolean found = listJoke.contains(newJoke);
        if (!found) {
            jokeListTainted = true;
            listJoke.add(newJoke);
        }
        return found;
    }

    public synchronized boolean isListTainted() {
        return jokeListTainted;
    }

}
