# Nova User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Editing task details

Use `edit` to change one detail of an existing task while preserving its type, completion status,
list position, and all other details.

```text
edit <task number> <field> <new value>
```

The supported fields depend on the task type:

- Todos: `/description`
- Deadlines: `/description` and `/by`
- Events: `/description`, `/from`, and `/to`

For example, this changes only the end time of task 3. A four-digit time retains the endpoint's
existing date:

```text
edit 3 /to 1700
```

Nova shows the task before and after the edit:

```text
Got it. I've updated this task:
  Before: [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
  After:  [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 5:00 PM)
```

You can also supply a complete date or date-time in the formats accepted when adding tasks. A
date without a time changes the field to a date-only value. Edit one field per command. Nova
rejects fields that do not belong to the selected task type and event ranges whose end would be
before their start.

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
