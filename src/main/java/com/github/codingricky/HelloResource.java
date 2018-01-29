package com.github.codingricky;

import com.google.inject.Inject; 
import com.google.inject.name.Named;
import javax.ws.rs.GET; 
import javax.ws.rs.Path;

@Path("/hello")
public class HelloResource { 
   private final String message;

   @Inject
   public HelloResource(@Named("message") String message) {
      this.message = message;
   }

   @GET
   public String hello() {
      return message;
   }
}
