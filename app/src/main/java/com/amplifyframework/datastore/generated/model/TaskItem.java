package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the TaskItem type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "TaskItems")
public final class TaskItem implements Model {
  public static final QueryField ID = field("id");
  public static final QueryField NAME = field("name");
  public static final QueryField DESCRIPTION = field("description");
  public static final QueryField STATE = field("state");
  public static final QueryField FOUND_AT = field("taskItemFoundAtId");
  public static final QueryField FILE = field("file");
  public static final QueryField LOCATION = field("location");
    public final @ModelField(targetType="ID", isRequired = true) String id;
    public final @ModelField(targetType="String", isRequired = true) String name;
    public final @ModelField(targetType="String") String description;
    public final @ModelField(targetType="String") String state;
    public final @ModelField(targetType="Team") @BelongsTo(targetName = "taskItemFoundAtId", type = Team.class) Team foundAt;
    public final @ModelField(targetType="String") String file;
    public final @ModelField(targetType="String") String location;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getDescription() {
      return description;
  }
  
  public String getState() {
      return state;
  }
  
  public Team getFoundAt() {
      return foundAt;
  }
  
  public String getFile() {
      return file;
  }
  
  public String getLocation() {
      return location;
  }
  
  private TaskItem(String id, String name, String description, String state, Team foundAt, String file, String location) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.state = state;
    this.foundAt = foundAt;
    this.file = file;
    this.location = location;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      TaskItem taskItem = (TaskItem) obj;
      return ObjectsCompat.equals(getId(), taskItem.getId()) &&
              ObjectsCompat.equals(getName(), taskItem.getName()) &&
              ObjectsCompat.equals(getDescription(), taskItem.getDescription()) &&
              ObjectsCompat.equals(getState(), taskItem.getState()) &&
              ObjectsCompat.equals(getFoundAt(), taskItem.getFoundAt()) &&
              ObjectsCompat.equals(getFile(), taskItem.getFile()) &&
              ObjectsCompat.equals(getLocation(), taskItem.getLocation());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getDescription())
      .append(getState())
      .append(getFoundAt())
      .append(getFile())
      .append(getLocation())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("TaskItem {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("state=" + String.valueOf(getState()) + ", ")
      .append("foundAt=" + String.valueOf(getFoundAt()) + ", ")
      .append("file=" + String.valueOf(getFile()) + ", ")
      .append("location=" + String.valueOf(getLocation()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static TaskItem justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new TaskItem(
      id,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      description,
      state,
      foundAt,
      file,
      location);
  }
  public interface NameStep {
    BuildStep name(String name);
  }
  

  public interface BuildStep {
    TaskItem build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep description(String description);
    BuildStep state(String state);
    BuildStep foundAt(Team foundAt);
    BuildStep file(String file);
    BuildStep location(String location);
  }
  

  public static class Builder implements NameStep, BuildStep {
    private String id;
    private String name;
    private String description;
    private String state;
    private Team foundAt;
    private String file;
    private String location;
    @Override
     public TaskItem build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new TaskItem(
          id,
          name,
          description,
          state,
          foundAt,
          file,
          location);
    }
    
    @Override
     public BuildStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep description(String description) {
        this.description = description;
        return this;
    }
    
    @Override
     public BuildStep state(String state) {
        this.state = state;
        return this;
    }
    
    @Override
     public BuildStep foundAt(Team foundAt) {
        this.foundAt = foundAt;
        return this;
    }
    
    @Override
     public BuildStep file(String file) {
        this.file = file;
        return this;
    }
    
    @Override
     public BuildStep location(String location) {
        this.location = location;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String name, String description, String state, Team foundAt, String file, String location) {
      super.id(id);
      super.name(name)
        .description(description)
        .state(state)
        .foundAt(foundAt)
        .file(file)
        .location(location);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder state(String state) {
      return (CopyOfBuilder) super.state(state);
    }
    
    @Override
     public CopyOfBuilder foundAt(Team foundAt) {
      return (CopyOfBuilder) super.foundAt(foundAt);
    }
    
    @Override
     public CopyOfBuilder file(String file) {
      return (CopyOfBuilder) super.file(file);
    }
    
    @Override
     public CopyOfBuilder location(String location) {
      return (CopyOfBuilder) super.location(location);
    }
  }
  
}
