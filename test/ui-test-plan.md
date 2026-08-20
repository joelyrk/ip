# Nova UI Test Plan

Run the cases with the repository-local `test-ui` skill. Each case starts a fresh Nova process. The expected output is the program's complete standard output; comparisons normalize CRLF to LF and ignore trailing newline characters only.

The program is compiled from `src/main/java/*.java`, runs with main class `Nova`, and requires Java 25. The test runner stops immediately after the first compilation, runtime, or output-comparison failure.

## TC-01: Add and list tasks

### Aim

Verify that entered task descriptions are stored in order and displayed as incomplete tasks.

### Input

```text
read book
return book
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
 added: read book
____________________________________________________________
____________________________________________________________
 added: return book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[ ] read book
 2.[ ] return book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-02: Mark a task as done

### Aim

Verify that `mark` changes the selected task's status and that `list` displays the completed task with an `X`.

### Input

```text
read book
return book
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
 added: read book
____________________________________________________________
____________________________________________________________
 added: return book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [X] return book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[ ] read book
 2.[X] return book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-03: Reverse a task's done status

### Aim

Verify that `unmark` changes a completed task back to not done and that `list` displays a blank status icon.

### Input

```text
read book
return book
mark 2
unmark 2
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
 added: read book
____________________________________________________________
____________________________________________________________
 added: return book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [X] return book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [ ] return book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[ ] read book
 2.[ ] return book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
