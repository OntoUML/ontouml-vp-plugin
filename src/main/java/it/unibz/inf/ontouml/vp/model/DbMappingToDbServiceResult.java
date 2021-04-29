package it.unibz.inf.ontouml.vp.model;

import java.util.List;

public class DbMappingToDbServiceResult extends ServiceResult<String> {

	  public DbMappingToDbServiceResult(String result, List<ServiceIssue> issues) {
	    super(result, issues);
	  }

	  public DbMappingToDbServiceResult() {
	    super();
	  }
	  
	  public String getSchema() {
		  return "schema";
	  }
	  
	  public String getObda() {
		  return "obda";
	  }
	  
	  public String getProperties() {
		  return "properties";
	  }

	  @Override
	  public String getMessage() {
	    return "The mapping to database was concluded";
	  }
	}
