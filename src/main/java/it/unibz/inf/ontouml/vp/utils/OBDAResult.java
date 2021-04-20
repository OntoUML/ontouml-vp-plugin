package it.unibz.inf.ontouml.vp.utils;


public class OBDAResult {

  private String schema;
  private String obdaFile;
  private String connection;

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getSchema() {
    return schema;
  }

  public void setObdaFile(String obdaFile) {
    this.obdaFile = obdaFile;
  }

  public String getObdaFile() {
    return obdaFile;
  }

  public void setConnection(String connection) {
    this.connection = connection;
  }

  public String getConnection() {
    return connection;
  }

  /*
  private String schema;
  private String obdaFile;
  private String connection;
  private BufferedReader buffer;

  public OBDAResult(BufferedReader buffer) {
  	this.buffer = buffer;
  	putVariables();
  }

  public String getSchema() {
  	return schema;
  }

  public String getOBDAFile() {
  	return obdaFile;
  }

  private void putVariables() {
  	StringBuilder auxSchema = new StringBuilder();
  	StringBuilder auxObda = new StringBuilder();

  	int value;
  	try {
  		value = buffer.read();

  		while ((char) value != '|' && value != -1) {
  			auxObda.append((char) value);
  			value = buffer.read();
  		}

  	    while ((value = buffer.read()) != -1) {
  	    	auxSchema.append((char) value);
  	    }
  	    schema = auxSchema.toString().trim();
  	    obdaFile = auxObda.toString().trim();
  	} catch (IOException e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();

  	}
  }

  private void putVariables() {
  	StringBuilder auxVar = new StringBuilder();
  	StringBuilder auxValue = new StringBuilder();
  	char val;

  	val = getChar();

  	if(val == '{') {

  	}

  }

  private char getChar() {
  	int val;
  	try {
  		if ((val = buffer.read()) != -1 ) {
  			return (char)val;
  		}
  	} catch (IOException e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();
  	}
  	return '|';

  }
  */
}
