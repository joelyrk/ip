# Nova UI Test Plan

Run the cases with the repository-local `test-ui` skill. Each case starts a fresh Nova process. The expected output is the program's complete standard output; comparisons normalize CRLF to LF and ignore trailing newline characters only.

The program is compiled recursively from `src/main/java`, runs with main class `nova.Nova`, and requires Java 25. The test runner stops immediately after the first compilation, runtime, or output-comparison failure.

## TC-01: Add and list all task types

### Aim

Verify that todos, deadlines, and events are stored polymorphically, displayed in order with their type icons, and format typed dates and times readably.

### Input

```text
todo read book
deadline return book /by 2/12/2019 1800
event project meeting /from 2019-12-03 1400 /to 2019-12-03 1600
deadline do homework /by 2019-12-04
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
   [D][ ] return book (by: Dec 02 2019, 6:00 PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] do homework (by: Dec 04 2019)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
 3.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
 4.[D][ ] do homework (by: Dec 04 2019)
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
deadline return book /by 2019-12-02
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
   [D][ ] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][X] return book (by: Dec 02 2019)
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
   [E][ ] orientation week (from: Oct 04 2019 to: Oct 11 2019)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] orientation week (from: Oct 04 2019 to: Oct 11 2019)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [E][ ] orientation week (from: Oct 04 2019 to: Oct 11 2019)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] orientation week (from: Oct 04 2019 to: Oct 11 2019)
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
 OOPS!!! I don't recognize that command. Start with todo, deadline, event, on, list, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
 OOPS!!! You entered a blank command. Try todo, deadline, event, on, list, mark, unmark, delete, or bye.
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
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
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
   [D][ ] return book (by: Jun 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
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
   [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] join sports club
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2019)
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

## TC-09: Save the task list after every change

### Aim

Verify that Nova creates a missing data folder and file, then rewrites the file after adding, marking, and deleting tasks.

### Input

```text
todo read book
deadline return book /by 2019-06-06
mark 2
delete 1
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
   [D][ ] return book (by: Jun 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected data file

```text
D | 1 | return book | 2019-06-06
```

## TC-10: Load saved tasks when Nova starts

### Aim

Verify that Nova loads saved todos, deadlines, and events in order and restores each task's completion state.

### Initial data file

```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 1 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
```

### Input

```text
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
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: Jun 06 2019)
 3.[E][X] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-11: Recover from corrupted saved data

### Aim

Verify that an invalid completion state produces an actionable startup warning and Nova continues with an empty task list.

### Initial data file

```text
T | yes | read book
```

### Input

```text
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
 OOPS!!! I couldn't load your saved tasks because line 1 is invalid: the completion state must be 0 or 1. Starting with an empty task list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-12: Recover when the data file cannot be written

### Aim

Verify that read and write failures are explained without crashing and a failed addition is rolled back in memory.

### Initial data path

```text
directory
```

### Input

```text
todo read book
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
 OOPS!!! I couldn't read the task data file. Starting with an empty task list.
____________________________________________________________
____________________________________________________________
 OOPS!!! I couldn't save the task data file. Your latest change was not kept.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-13: Preserve storage separator characters in task text

### Aim

Verify that escaped pipes and backslashes in task descriptions load correctly and remain escaped when the updated task list is saved again.

### Initial data file

```text
D | 0 | discuss \| review \\ notes | 2019-08-09 1700
```

### Input

```text
mark 1
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
 Nice! I've marked this task as done:
   [D][X] discuss | review \ notes (by: Aug 09 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][X] discuss | review \ notes (by: Aug 09 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected data file

```text
D | 1 | discuss \| review \\ notes | 2019-08-09 1700
```

## TC-14: Roll back every task mutation when saving fails

### Aim

Verify that failed mark, unmark, and delete saves restore the original in-memory task states and list order.

### Initial data file

```text
T | 0 | read book
D | 1 | return book | 2019-12-02
```

### Initial data path

```text
read-only-directory
```

### Input

```text
mark 1
unmark 2
delete 1
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
 OOPS!!! I couldn't save the task data file. Your latest change was not kept.
____________________________________________________________
____________________________________________________________
 OOPS!!! I couldn't save the task data file. Your latest change was not kept.
____________________________________________________________
____________________________________________________________
 OOPS!!! I couldn't save the task data file. Your latest change was not kept.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-15: Find scheduled tasks by date

### Aim

Verify that Nova parses real dates and times into typed values, finds deadlines and multi-day events on a requested date, excludes todos, reports an empty result, and explains invalid dates or ranges.

### Input

```text
deadline return book /by 2/12/2019 1800
event conference /from 1/12/2019 /to 3/12/2019
todo undated
on 2/12/2019
on 4/12/2019
deadline impossible /by 31/2/2019
event backwards /from 2019-12-03 /to 2019-12-02
on
on 2019-13-01
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
   [D][ ] return book (by: Dec 02 2019, 6:00 PM)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] conference (from: Dec 01 2019 to: Dec 03 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] undated
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks occurring on Dec 02 2019:
 1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
 2.[E][ ] conference (from: Dec 01 2019 to: Dec 03 2019)
____________________________________________________________
____________________________________________________________
 Here are the tasks occurring on Dec 04 2019:
 No deadlines or events occur on this date.
____________________________________________________________
____________________________________________________________
 OOPS!!! The /by date/time must be a real date in yyyy-MM-dd or d/M/yyyy format, optionally followed by HHmm, for example: 2019-12-02 1800.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event's /to date/time cannot be before its /from date/time.
____________________________________________________________
____________________________________________________________
 OOPS!!! Tell me which date to search. Try: on 2019-12-02.
____________________________________________________________
____________________________________________________________
 OOPS!!! The date after on must be a real date in yyyy-MM-dd or d/M/yyyy format, for example: on 2019-12-02.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
