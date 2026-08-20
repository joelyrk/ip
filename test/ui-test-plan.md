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
