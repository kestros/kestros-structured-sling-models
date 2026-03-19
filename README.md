# Kestros Structured Sling Models

Foundational Sling Model framework providing base model types, structured resource adaption, and model interaction utilities for Kestros/Sling instances.

## Purpose

`kestros-structured-sling-models` is the core model layer of the Kestros architecture. It provides `BaseResource`, `BasePage`, and `BaseSlingModel` -- the base classes that all Kestros Sling Models extend. It also provides `SlingModelUtils`, a utility class for type-safe resource adaption based on `sling:resourceType` matching.

Every Kestros module that defines Sling Models depends on this bundle. If you are creating a new model, component, or page type, you will extend one of the base classes provided here.

## Installation & Build

**Maven coordinates:**

```xml
<dependency>
  <groupId>io.kestros.commons</groupId>
  <artifactId>kestros-structured-sling-models</artifactId>
  <version>0.2.5</version>
</dependency>
```

**Build:**

```bash
mvn clean package
```

**Deploy to a Sling instance:**

```bash
curl -u admin:admin \
  -F "action=install" \
  -F "bundlestart=true" \
  -F "bundlefile=@target/kestros-structured-sling-models-0.2.5.jar" \
  "http://localhost:8080/system/console/bundles"
```

## Configuration

No OSGi configuration required. This bundle provides model classes and utilities only.

## API / Service Usage

### Core Model Types

#### `BaseSlingModel` (Interface)

The root interface for all Kestros Sling Models.

```java
public interface BaseSlingModel {
    Resource getResource();
}
```

#### `BaseResource`

Baseline Sling Model for all resource types. Adapts from `Resource` and provides property access, resource type resolution, and metadata retrieval.

```java
@Model(adaptables = Resource.class, resourceType = "sling/servlet/default")
public class BaseResource implements BaseSlingModel
```

**Key methods:**

| Method | Returns | Description |
|--------|---------|-------------|
| `getResource()` | `Resource` | The underlying JCR resource |
| `getName()` | `String` | Resource name |
| `getPath()` | `String` | Resource path |
| `getTitle()` | `String` | `jcr:title` value, or resource name if not set |
| `getDescription()` | `String` | `jcr:description` value, or empty string |
| `getResourceType()` | `String` | `sling:resourceType`, falling back to `jcr:primaryType` |
| `getSlingResourceType()` | `String` | `sling:resourceType` property only |
| `getResourceSuperType()` | `String` | `sling:resourceSuperType` property |
| `getJcrPrimaryType()` | `String` | `jcr:primaryType` property |
| `getParent()` | `BaseResource` | Parent resource adapted to BaseResource |
| `getProperty(key, defaultValue)` | `<T>` | Property value from the resource's ValueMap |
| `getResourceResolver()` | `ResourceResolver` | Resolver associated with this resource |
| `getLastModifiedDate()` | `Date` | `jcr:lastModified` value |
| `getCreatedDate()` | `Date` | `jcr:created` value |

**Usage -- extending BaseResource:**

```java
@Model(adaptables = Resource.class, resourceType = "myapp/myresource")
public class MyResource extends BaseResource {

    public String getMyProperty() {
        return getProperty("myProperty", "default value");
    }
}
```

#### `BasePage`

Base model for page types. Extends `BaseResource` and automatically handles `jcr:content` child resources -- property access transparently reads from the `jcr:content` node when present.

```java
@Model(adaptables = Resource.class)
public class BasePage extends BaseResource
```

If the model is adapted from a `jcr:content` resource, `BasePage` automatically resolves to the parent page resource, so `getName()`, `getPath()`, and `getProperties()` always refer to the page level.

#### `BaseRequestContext`

Base model for request-scoped contexts. Adapts from `SlingHttpServletRequest`.

### Annotations

#### `@KestrosModel`

Marks a class as a Kestros model. Supports optional `contextModel` and `docPaths` attributes.

```java
@KestrosModel(contextModel = BaseRequestContext.class,
              docPaths = {"/content/guide-articles/..."})
```

#### `@KestrosProperty`

Documents model properties with description and JCR property name.

```java
@KestrosProperty(description = "Title of the resource.",
                 jcrPropertyName = "jcr:title",
                 configurable = true)
public String getTitle() { ... }
```

### SlingModelUtils

Static utility class for type-safe resource adaption. Uses the `@Model` annotation's `resourceType` value to validate adaption.

| Method | Description |
|--------|-------------|
| `adaptTo(resource, type)` | Adapts a resource to a type; throws `InvalidResourceTypeException` if resource type does not match |
| `getResourceAsType(path, resolver, type)` | Gets a resource by path and adapts to the specified type |
| `getResourceAsBaseResource(path, resolver)` | Gets a resource by path as `BaseResource` |
| `getChildAsType(name, resource, type)` | Gets a named child resource and adapts to a type |
| `getChildrenOfType(resource, type)` | Returns all children that can be adapted to the specified type |
| `getChildrenAsClosestTypes(resource, modelFactory)` | Returns children adapted to their closest matching registered model type |
| `getResourceAsClosestType(resource, modelFactory)` | Dynamically adapts a resource to its closest registered model type |
| `getParentResourceAsBaseResource(model)` | Gets the parent resource as `BaseResource` |
| `getFirstAncestorOfType(model, type)` | Walks up the tree to find the first ancestor matching the specified type |

**Usage examples:**

```java
// Get a resource by path as a specific type
try {
    MyResource res = SlingModelUtils.getResourceAsType(
        "/content/my-resource", resolver, MyResource.class);
} catch (InvalidResourceTypeException e) {
    // Resource type mismatch
} catch (ResourceNotFoundException e) {
    // Resource not found
}

// Get all children of a specific type
List<MyResource> children = SlingModelUtils.getChildrenOfType(parentResource, MyResource.class);

// Dynamic adaption based on registered model types
MyResource res = SlingModelUtils.getResourceAsClosestType(resource, modelFactory);
```

### FileModelUtils / FileUtils

Utility classes for working with JCR file resources (`nt:file`). Use `FileModelUtils.adaptToFileType()` to adapt file resources with type validation.

### File Types

#### `BaseFile`

Base model for file resources. Extend this instead of `BaseResource` when working with `nt:file` resources.

#### `FileType`

Interface defining a file type with extension and MIME type validation.

### Exceptions

| Exception | Thrown When |
|-----------|------------|
| `ResourceNotFoundException` | A resource at the specified path does not exist |
| `InvalidResourceTypeException` | A resource cannot be adapted to the target type due to resource type mismatch |
| `ChildResourceNotFoundException` | A named child resource does not exist |
| `NoParentResourceException` | The resource has no parent (root resource) |
| `NoValidAncestorException` | No ancestor matching the target type was found |
| `MatchingResourceTypeNotFoundException` | No registered model type matches the resource |
| `ModelAdaptionException` | Base exception for all model adaption failures |
| `JcrFileReadException` | A JCR file resource could not be read |

## Dependencies

### Upstream

| Dependency | Maven Coordinates |
|------------|-------------------|
| kestros-common-utils | `io.kestros.commons:kestros-common-utils:[0.0.3,0.0.99]` |
| Apache Sling API | `org.apache.sling:org.apache.sling.api` |
| Apache Sling Models API | `org.apache.sling:org.apache.sling.models.api` |
| Apache Jackrabbit | `org.apache.jackrabbit:jackrabbit-jcr-commons` |

### Downstream (modules that depend on this)

This is a foundational dependency used by nearly every Kestros module, including:

- `kestros-osgi-service-utils`
- `kestros-validation-api`
- `kestros-validation-core`
- `kestros-site-management-core`
- `kestros-content-objects`
- `kestros-sling-ui-libraries`
- `kestros-component-types-core`

## Contribution Notes

- **Branch from:** `develop`
- **PR target:** `develop`
- **Branch naming:** `{type}/TASK-NNN-short-description` (e.g. `fix/TASK-055-base-resource-null-check`)
- **Commit format:** `[kestros-structured-sling-models]: <action>, <brief result>`
- **Build verification:** Run `mvn clean package` before submitting; all tests must pass
