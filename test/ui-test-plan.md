# Nova UI Test Plan

Run the cases with the repository-local `test-ui` skill. Each case starts a fresh Nova process. The expected output is the program's complete standard output; comparisons normalize CRLF to LF and ignore trailing newline characters only.

The program is compiled from `src/main/java/*.java`, runs with main class `Nova`, and requires Java 25. The test runner stops immediately after the first compilation, runtime, or output-comparison failure.

## TC-01: Add and list all task types

### Aim

Verify that todos, deadlines, and events are stored polymorphically, displayed in order with their type icons, and retain date/time text verbatim.

### Input

```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
deadline do homework /by no idea :-p
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] do homework (by: no idea :-p)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
 4.[D][ ] do homework (by: no idea :-p)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-02: Mark a deadline as done

### Aim

Verify that `mark` works through the shared `Task` type and preserves deadline-specific display details.

### Input

```text
todo read book
deadline return book /by Sunday
mark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-03: Reverse an event's done status

### Aim

Verify that `unmark` changes a completed event back to not done while preserving its time range.

### Input

```text
event orientation week /from 4/10/2019 /to 11/10/2019
mark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] orientation week (from: 4/10/2019 to: 11/10/2019)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-04: Reject empty and unknown commands

### Aim

Verify that Nova uses specific exception messages for an empty todo, an unknown command, and a blank input while continuing to accept later commands.

### Input

```text
todo
blah

bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! A todo needs a description. Try: todo <description>.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't recognize that command. Start with todo, deadline, event, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! You entered a blank command. Try todo, deadline, event, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-07: Delete a task and renumber the list

### Aim

Verify that `delete` removes the selected task, reports it, updates the task count, and shifts later task numbers down.

### Input

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
delete 3
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: June 6th)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] join sports club
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: June 6th)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] join sports club
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: June 6th)
 3.[T][X] join sports club
 4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-08: Explain invalid delete commands

### Aim

Verify that missing, non-numeric, empty-list, and out-of-range delete task numbers produce actionable guidance without changing the list.

### Input

```text
delete
delete first
delete 1
todo read book
delete 0
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Tell me which task to delete. Try: delete <task number>.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number after delete must be a whole number, for example: delete 1.
____________________________________________________________
____________________________________________________________
 OOPS!!! There are no tasks to delete yet. Add a task first.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 0 does not exist. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 2 does not exist. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-05: Explain malformed deadlines and events

### Aim

Verify that every missing or misplaced deadline and event field produces guidance specific to that field.

### Input

```text
deadline
deadline return book
deadline /by Sunday
deadline return book /by
event
event meeting /to 4pm
event meeting /from 2pm
event meeting /to 4pm /from 2pm
event /from 2pm /to 4pm
event meeting /from /to 4pm
event meeting /from 2pm /to
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a description. Try: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a /by date or time. Try: deadline return book /by <date or time>.
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a description before /by.
____________________________________________________________
____________________________________________________________
 OOPS!!! The /by field cannot be empty. Add a date or time after /by.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a description and a time range. Try: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a /from start date or time.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a /to end date or time.
____________________________________________________________
____________________________________________________________
 OOPS!!! Put /from before /to. Try: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a description before /from.
____________________________________________________________
____________________________________________________________
 OOPS!!! The /from field cannot be empty. Add a start date or time after /from.
____________________________________________________________
____________________________________________________________
 OOPS!!! The /to field cannot be empty. Add an end date or time after /to.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-06: Explain invalid mark and unmark commands

### Aim

Verify that missing, non-numeric, empty-list, and out-of-range task numbers each produce actionable guidance.

### Input

```text
mark
unmark
mark first
unmark 1
todo read book
mark 0
mark 2
unmark two
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Tell me which task to mark. Try: mark <task number>.
____________________________________________________________
____________________________________________________________
 OOPS!!! Tell me which task to unmark. Try: unmark <task number>.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number after mark must be a whole number, for example: mark 1.
____________________________________________________________
____________________________________________________________
 OOPS!!! There are no tasks to unmark yet. Add a task first.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 0 does not exist. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 2 does not exist. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number after unmark must be a whole number, for example: unmark 1.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
