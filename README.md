# Dropwizard-Guice-sample

http://www.baeldung.com/guice

1. Introduction

This article will examine the fundamentals of Google Guice. We’ll look at approaches to completing basic Dependency Injection (DI) tasks in Guice.

We will also compare and contrast the Guice approach to those of more established DI frameworks like Spring and Contexts and Dependency Injection (CDI).

This article presumes the reader has an understanding of the fundamentals of the Dependency Injection pattern.
2. Setup

In order to use Google Guice in your Maven project, you will need to add the following dependency to your pom.xml:

	
<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
    <version>4.1.0</version>
</dependency>

There is also a collection of Guice extensions (we will cover those a little later) here, as well as third-party modules to extend the capabilities of Guice (mainly by providing integration to more established Java frameworks).
3. Basic Dependency Injection With Guice
3.1. Our Sample Application

We will be working with a scenario where we design classes that support three means of communication in a helpdesk business: Email, SMS, and IM.

Consider the class:
	
public class Communication {
  
    @Inject
    private Logger logger;
     
    @Inject
    private Communicator communicator;
 
    public Communication(Boolean keepRecords) {
        if (keepRecords) {
            System.out.println("Message logging enabled");
        }
    }
  
    public boolean sendMessage(String message) {
        return communicator.sendMessage(message);
    }
 
}

This Communication class is the basic unit of communication. An instance of this class is used to send messages via the available communications channels. As shown above, Communication has a Communicator which we use to do the actual message transmission.

The basic entry point into Guice is the Injector:

	
public static void main(String[] args){
    Injector injector = Guice.createInjector(new BasicModule());
    Communication comms = injector.getInstance(Communication.class);
}

This main method retrieves an instance of our Communication class. It also introduces a fundamental concept of Guice: the Module (using BasicModule in this example). The Module is the basic unit of definition of bindings (or wiring, as it’s known in Spring).

Guice has adopted a code-first approach for dependency injection and management so you won’t be dealing with a lot of XML out-of-the-box.

In the example above, the dependency tree of Communication will be implicitly injected using a feature called just-in-time binding, provided the classes have the default no-arg constructor. This has been a feature in Guice since inception and only available in Spring since v4.3.
3.2. Guice Bindings

Binding is to Guice as wiring is to Spring. With bindings, you define how Guice is going to inject dependencies into a class.

A binding is defined in an implementation of com.google.inject.AbstractModule:

	
public class BasicModule extends AbstractModule {
  
    @Override
    protected void configure() {
        bind(Communicator.class).to(DefaultCommunicatorImpl.class);
    }
}

This module implementation specifies that an instance of DefaultCommunicatorImpl is to be injected wherever a Communicator variable is found.

Another incarnation of this mechanism is the named binding. Consider the following variable declaration:
1
2
	
@Inject @Named("DefaultCommunicator")
Communicator communicator;

For this, we will have the following binding definition:

	
@Override
protected void configure() {
    bind(Communicator.class)
      .annotatedWith(Names.named("DefaultCommunicator"))
      .to(Communicator.class);
}

This binding will provide an instance of Communicator to a variable annotated with the @Named(“DefaultCommunicator”) annotation.

You’ll notice the @Inject and @Named annotations appear to be loan annotations from JavaEE’s CDI, and they are. They are in the com.google.inject.* package — you should be careful to import from the right package when using an IDE.

Tip: While we just said to use the Guice-provided @Inject and @Named, it’s worthwhile to note that Guice does provide support for javax.inject.Inject and javax.inject.Named, among other JavaEE annotations.

You can also inject a dependency that doesn’t have a default no-arg constructor using constructor binding:
	
public class BasicModule extends AbstractModule {
  
    @Override
    protected void configure() {
        bind(Boolean.class).toInstance(true);
        bind(Communication.class).toConstructor(
          Communication.class.getConstructor(Boolean.TYPE));
}

The snippet above will inject an instance of Communication using the constructor that takes a boolean argument. We supply the true argument to the constructor by defining an untargeted binding of the Boolean class.

This untargeted binding will be eagerly supplied to any constructor in the binding that accepts a boolean parameter. With this approach, all dependencies of Communication are injected.

Another approach to constructor-specific binding is the instance binding, where we provide an instance directly in the binding:
	
public class BasicModule extends AbstractModule {
  
    @Override
    protected void configure() {
        bind(Communication.class)
          .toInstance(new Communication(true));
    }    
}

This binding will provide an instance of the Communication class wherever a Communication variable is declared.

In this case, however, the dependency tree of the class will not be automatically wired. You should limit the use of this mode where there isn’t any heavy initialization or dependency injection necessary.
4. Types of Dependency Injection

Guice supports the standard types of injections you would have come to expect with the DI pattern. In the Communicator class, we need to inject different types of CommunicationMode.
