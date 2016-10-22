package seedu.todoList.model.task;

import seedu.todoList.commons.util.CollectionUtil;
import seedu.todoList.model.task.attributes.Date;
import seedu.todoList.model.task.attributes.EndDate;
import seedu.todoList.model.task.attributes.Name;
import seedu.todoList.model.task.attributes.Priority;

/**
 * Represents a task in the TodoList.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Todo extends Task implements ReadOnlyTask {
    
    private Date date;
    private EndDate endDate;
    private Priority priority;

    /**
     * Every field must be present and not null.
     * @param date 
     */
    public Todo(Name name, Date date, EndDate endDate, Priority priority) {
        assert !CollectionUtil.isAnyNull(name, date, endDate, priority);
        super.name = name;
        this.date = date;
        this.endDate = endDate;
        this.priority = priority;
    }

    /**
     * Copy constructor.
     */
    public Todo(Todo source) {
        this(source.getName(), source.getDate() , source.getEndDate(), source.getPriority());
    }

    public Date getDate() {
        return date;
    }
    
    public EndDate getEndDate() {
        return endDate;
    }

    public Priority getPriority() {
        return priority;
    }
    
    public Todo(ReadOnlyTask source) {
    	this((Todo) source);
    };

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Todo // instanceof handles nulls
                && super.name.equals(((Todo) other).getName()))
                && this.date.equals(((Todo) other).getDate())
                && this.endDate.equals(((Todo) other).getEndDate())
                && this.priority.equals(((Todo) other).getPriority());
    }

    @Override
    public String toString() {
    	final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Date: ")
                .append(getDate())
                .append(" End Date: ")
                .append(getEndDate())
                .append(" Priority: ")
                .append(getPriority());
        return builder.toString();
    }

	public Todo getTodo() {
		// TODO Auto-generated method stub
		return null;
	}

}