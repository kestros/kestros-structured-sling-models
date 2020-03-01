# Structured Sling Models

A baseline framework that builds off of Sling Models to provide a structured and consistent way to
 build and adapt to Sling Models.

## Install
To add this bundle to a project, add the following dependency to your `pom.xml`.
```
<dependencies>
  <dependency>
    <groupId>io.kestros.commons</groupId>
      <artifactId>kestros-structured-sling-models</artifactId>
    <version>--version--</version>
  </dependency>
</dependencies>
```
The bundle can either be installed manually from the Felix console, or embedded into a content-package.

## Extending BaseResource

BaseResource is a simple Sling Model that adapts from Sling Resource.  It provides general information about the Resource such as path, name title. More importantly, it provides a baseline for the structured adaption framework.  In order to make full use of all of the functionality in this bundle, it is recommended that your Sling Models (which adapt from Resource.class) extend BaseResource (or another Model that is a descendent of BaseResource).  
```
@Model(adaptables = Resource.class, resourceType = "myapp/myresource")
public class MyResource extends BaseResource {
```

### Getting Property Values
To retrieve a resource's property values either the `getProperty` or `getProperties` method can be used.
```
public String getMyProperty() {
  return getProperty("myProperty", "default value");
}
```
```
public boolean getMyProperty() {
  return getProperty("myProperty", Boolean.FALSE);
}
```
```
public String getMyProperty() {
  return getProperties().get("myProperty", "default value");
}
```
```
public boolean getMyBooleanProperty() {
  return getProperties().get("myBooleanProperty", Boolean.FALSE);
}

```

## Structured Model Adaption
`SlingModelUtils` has been provided to make retrieving resources as compatible model types easier.  To make use of this, Models should be configured using the `@Model` annotation with `resourceType` configured.

```
@Model(adaptables = Resource.class, resourceType = "myapp/myresource")
public class MyResource extends BaseResource {
```
### Get Resources As Specified Type
`SlingModelUtils` is a utility Class that helps adapt Resources to their Model types.  If There is an issue retrieving the adapted Resource, a specific Exception type will be thrown, giving more context as to why the adaption failed.  All thrown Exceptions extend `ModelAdaptionException`, which can be used as a catch-all. 
#### Get Resource by Path

To retrieve a resource as a specific type, use `SlingModelUtils.getResourceAsType`.  If the requested Resource cannot be adapted to the specified Model type, `InvalidResourceTypeException` will be thrown.  If the Resource is not found, `ResourceNotFoundException` will be thrown.
```
try {
      SlingModelUtils.getResourceAsType("/content/my-resource", getResourceResolver(), MyResource.class);
    } catch (InvalidResourceTypeException exception) {
      // resource could not be adapted to the Model Type
    } catch (ResourceNotFoundException exception) {
      // No resource was found
    }
```

#### Get Child Resource
To retrieve a child Resource as a specific type, use `SlingModelUtils.getChildAsType`.
```
try {
      SlingModelUtils.getChildAsType("child", this, MyResource.class);
    } catch (InvalidResourceTypeException exception) {
      `child` was found, but could not be adapted to `MyResource.class`
    } catch (ChildResourceNotFoundException exception) {
      // No child with the name `child` was found. 
    }
```

#### Get All Children of Type
To retrieve all child Resources as a specific type, use `SlingModelUtils.getChildrenOfType`.  If a child cannot be adapted to the specified type, it will be excluded from the returned `List`.
```
SlingModelUtils.getChildrenOfType(this,MyResource.class);
```

### Dynamic Model Adaption
Resources can be dynamically adapted to a Model type, using `SlingModelUtils.getResourceAsClosestType`.  If no Model types match the `sling:resourceType` or `jcr:primaryType` of the Resource, `InvalidResourceTypeException` will be thrown.
```
try {
      SlingModelUtils.getResourceAsClosestType(resource, modelFactory);
    } catch (InvalidResourceTypeException exception) {
      // No matching Model type was found.
    }
```

## Model Validation

A structured Model validation framework has been provided, which can be used on any Model that 
extends `BaseResource` or `BaseSlingRequest`.

Validation messages can be configured to one of three levels:
* `ModelValidationMessageType.ERROR` 
* `ModelValidationMessageType.WARNING`
* `ModelValidationMessageType.INFO`

### Adding a Validation Service to a Model.
A Model Validation Service can be attached to a Model Type using the `@StructuredModel` annotation.
  This will set `ModelValidators` to automatically run after a Model is initially adapted.
```
@StructuredModel(validationService = MyResourceModelValidationService.class)
@Model(adaptables = Resource.class, resourceType = "myapp/myresource")
public class MyResource extends BaseResource {
}
```

### Creating the Validation Service Class
To create a new `ModelValidationService`, extend the `ModelValidationService` class.
```
public class MyresourecValidationService extends ModelValidationService {

  @Override
  public <T extends BaseSlingModel> T getModel() {
    return null;
  }

  @Override
  public void registerValidators() {

  }
}
```
The `getModel()` method, will have to be updated to return the Model type of the Model that the
 Service validates.
```
  @Override
  public MyResource getModel() {
    return (MyResource) getGenericModel();
  }

```

#### Creating ModelValidators
The `ModelValidator` interface provides a structure for consistent and modular validation.

The following Methods must be configured:
* `isValid` the boolean logic that determines whether the current Model passes the current `ModelValidator`.
* `getMessage` the message to be shown when the model fails this validator.
* `isAlwaysRun` whether or not to always run the current validator during Sling Model adaption.  
If false, the current validator will only be checked during `model.doDetailedValidation()`.  It is 
generally recommended to return false when the Validator must look to another Model type when 
determining validity, because this will trigger that model's validation (as well as subsequent model 
validation).  Running detailed validation only when it is needed can potentially save a significant 
amount of time/processing power when adapt large quantities of models.
* `getType` the level the message should be shown at if the validator fails. 

To create a new `ModelValidator`, create a new implementation of `ModelValidator`
```
public ModelValidator isPropertyValueValid() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return StringUtils.isNotEmpty(getModel().getName());
      }

      @Override
      public String getMessage() {
        return "Has a name";
      }

      @Override
      public boolean isAlwaysRun() {
        return true;
      }

      @Override
      public ModelValidationMessageType getType() {
        //ModelValidationMessageType.ERROR
        //ModelValidationMessageType.WARNING
        //ModelValidationMessageType.INFO
        return ModelValidationMessageType.ERROR;
      }
    };
  }
```



#### Creating ModelValidatorBundle
`ModelValidatorBundle` is an abstract implementation of `ModelValidator` that can be extended to
 provided more structured validation logic. A `ModelValidatorBundle` has one or more 
 `ModelValidators` or `ModelValidatorBundles` added to it, one or all of which must pass for the 
 bundle to pass (depending on the configuration). 


To create a ModelValidator bundle,  
```
new ModelValidatorBundle() {
      @Override
      public boolean isAlwaysRun() {
        return false;
      }

      @Override
      public ModelValidationMessageType getType() {
        return null;
      }

      @Override
      public void registerValidators() {

      }

      @Override
      public boolean isAllMustBeTrue() {
        return false;
      }
    };
```
#### Register validators to a ModelValidatorBundle
Multiple `ModelValidator` and `ModelValidatorBundle` implementations can be added to a 
`ModelValidatorBundle`. To do so, pass those to the `addBasicValidator` method, inside of the overridden
 `reigsterValidators` method. 
```
@Override
public void registerValidators() {
  addBasicValidator(myValidator);
  addBasicValidator(myValidatorBundle);
}
```

##### All Validators Must Pass
For all validators in a bundle to pass in order for the current `ModelValidatorBundle` to be 
considered valid, return `true` for `isAllMustBeTrue`.
``` 
@Override
public boolean isAllMustBeTrue() {
 return true;
}
```

##### One Validator Must Pass
For only one validator in a bundle to pass in order for the current `ModelValidatorBundle` to be 
considered valid, return `false` for `isAllMustBeTrue`.
``` 
@Override
public boolean isAllMustBeTrue() {
 return true;
}
```


### Model Validation Using @PostConstruct
Validation logic can also be added to the Model type in the `@PostConstruct` method.

```
@PostConstruct
@Override
public void validate() {
  if(StringUtils.isEmpty(getMyProperty()) {
    addErrorMessage("My error message.");
    // addWarningMessage("My warning message.");
    // addInfoMessage("My info message.");
  }
}
``` 

#### Registering ModelValidators to the Service
To add a `ModelValidator` or `ModelValidatorBundle` to a `ModelValidationService`, so that it is 
automatically run when a model is constructed, pass it into the `addBasicValidator` method inside of the
`ModelValidationService`'s overridden `registerValidators` method.

```
@Override
public void registerValidators() {
  addBasicValidator(myValidator);
  addBasicValidator(myValidatorBundle);
}
```

#### Using Common ModelValidators
Common validators have been provided in the `CommonValidators` class.

## Adapting File Resources
Since all file Resources have a `jcr:primaryType` of `nt:file`, `InvalidResourceTypeException` can 
never be thrown when using `SlingModelUtils`.  To solve for this, `BaseFile` should be extended 
instead of `BaseResource`.  When adapting a Resource to the new Class, use
 `FileModelUtils.adaptToFileType(myResource, MyFileType.class)`. If there are any validation errors,
 `InvalidResourceTypeException` will be thrown.