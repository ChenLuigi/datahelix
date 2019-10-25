# Table of Contents
1. [Introduction](#Introduction)

2. [Profiles](#Profiles)
    1. [Creating a Profile](#Creating-a-Profile)
    2. [Example Profile](#Example-Profile)

3. [Fields](#Fields)
    1. [Name](#fields-name)
    1. [Type](#fields-type)
    1. [Nullable](#fields-nullable)
    1. [Formatting](#fields-formatting)
    1. [Unique](#fields-unique)

4. [Data types](#Data-Types)
    1. [Integer/Decimal](#Integer/Decimal)
    2. [Strings](#Strings)
    3. [DateTime](#DateTime)
    4. [Custom Data Types](#Custom-Data-Types)

5. [Predicate constraints](#Predicate-constraints)
    1. [Theory](#Theory)
    2. [General constraints](#General-constraints)
        1. [equalTo](#predicate-equalto)
        2. [inSet](#predicate-inset)
        2. [inMap](#predicate-inmap)
        3. [null](#predicate-null)
    3. [String constraints](#String-constraints)
        1. [matchingRegex](#predicate-matchingregex)
        2. [containingRegex](#predicate-containingregex)
        3. [ofLength](#predicate-oflength)
        4. [longerThan](#predicate-longerthan)
        5. [shorterThan](#predicate-shorterthan)
    4. [Integer/Decimal constraints](#Integer/Decimal-constraints)
        1. [greaterThan](#predicate-greaterthan)
        2. [greaterThanOrEqualTo](#predicate-greaterthanorequalto)
        3. [lessThan](#predicate-lessthan)
        4. [lessThanOrEqualTo](#predicate-lessthanorequalto)
        5. [granularTo](#predicate-granularto)
    5. [DateTime constraints](#DateTime-constraints)
        1. [after](#predicate-after)
        2. [afterOrAt](#predicate-afterorat)
        3. [before](#predicate-before)
        4. [beforeOrAt](#predicate-beforeorat)
        5. [granularTo](#predicate-granularto-datetime)
    6. [Dependent field constraints](#otherfield-constraints)
        1. [otherField](#predicate-otherfield)
        2. [offset](#predicate-offset)

6. [Grammatical constraints](#Grammatical-Constraints)
    1. [not](#not)
    2. [anyOf](#anyOf)
    3. [allOf](#allOf)
    4. [if](#if)

7. [Running a Profile](#Running-a-Profile)
    1. [Command Line Arguments](#Command-Line-Arguments)
        1. [Command Line Arguments for Generate Mode](#Command-Line-Arguments-for-Generate-Mode)
    2. [Generation Strategies](#Generation-strategies)
        1. [Random Mode](#Random-Mode)
        2. [Full Sequential Mode](#Full-Sequential-Mode)
            1. [Combination Strategies](#Combination-Strategies)
                1. [Minimal](#Minimal)
                2. [Exhaustive](#Exhaustive)
                3. [Pinning](#Pinning)

8. [Visualising Decision Trees](#Visualising-Decision-Trees)

# Introduction

This guide outlines how to create a profile and contains information on the syntax of the DataHelix schema.

* If you are new to DataHelix, please read the [Getting Started page](GettingStarted.md)

* If you would like information on how to contribute to the project as well as a technical overview of the key concepts and structure of   the DataHelix then see the [Developer Guide](DeveloperGuide.md).

# Profiles

## Creating a Profile

This section will walk you through creating basic profiles with which you can generate data.

Profiles are JSON documents consisting of three sections, the schema version, the list of fields and the rules.

- **Schema Version** - Dictates the method of serialisation of the profile in order for the generator to
interpret the profile fields and rules. The latest version is 0.7.
```
    "schemaVersion": "0.7",
```
- **List of Fields** - An array of column headings is defined with unique "name" keys.
```
    "fields": [
        {
            "name": "Column 1"
        },
        {
            "name": "Column 2"
        }
    ]
```
- **Rules** - an array of constraints defined with a description. Constraints reduce the data in each column from the [universal set](user/SetRestrictionAndGeneration.md)
to the desired range of values. They are formatted as JSON objects. There are three types of constraints:

    - [Predicate Constraints](#Predicate-constraints) - predicates that define any given value as being
    _valid_ or _invalid_
    - [Grammatical Constraints](#Grammatical-constraints) - used to combine or modify other constraints

Here is a list of two rules comprised of one constraint each:

```
    "rules": [
        {
          "rule": "Column 1 is a string",
          "constraints": [
            {
              "field": "Column 1",
              "equalTo": "foo"
            }
          ]
        },
        {
          "rule": "Column 2 is a number",
          "constraints": [
            {
              "field": "Column 2",
              "equalTo": "bar"
            }
          ]
        }
      ]

``'

These three sections are combined to form the [complete profile](#Example-Profile).

## Example Profile

    {
    "schemaVersion": "0.7",
    "fields": [
        {
            "name": "Column 1",
            "type": "string"
        },
        {
            "name": "Column 2",
            "type": "integer"
        }
    ],
    "rules": [
        {
        "rule": "Column 1 is foo",
        "constraints": [
            {
            "field": "Column 1",
            "equalTo": "foo"
            }
        ]
        }
    ]
    }

* For a larger profile example see [here](user/Schema.md)
* Further examples can be found in the Examples folder [here](https://github.com/finos/datahelix/tree/master/examples)

# Fields

Fields are the "slots" of data that can take values. Typical fields might be _email_address_ or _user_id_. By default, any piece of data is valid for a field. This is an example field object for the profile:

```javascript
{
    "name": "field1",
    "type": "decimal",
    "nullable": false,
    "formatting": "%.5s",
    "unique": true
}
```

Each of the field properties are outlined below:

<div id="fields-name"></div>

## `name`

Name of the field in the output which has to be unique within the `Fields` array. If generating into a CSV or database, the name is used as a column name. If generating into JSON, the name is used as the key for object properties.

This is a required property.


<div id="fields-type"></div>

## `type`

The data type of the field. See [Data types](#Data-Types) for more on how types work within DataHelix. Valid options are
* `decimal`
*  `integer`
*  `string`
*  `date`
*  `datetime`
*  `ISIN`
*  `SEDOL`
*  `CUSIP`
*  `RIC`
*  `firstname`
*  `lastname`
*  `fullname`

 This is a required property.


<div id="fields-nullable"></div>

## `nullable`

Sets the field as nullable. When set to false it is the same as a [not](#not) [null](#predicate-null) constraint for the field.

This is an optional property of the field and will default to true.

<div id="fields-formatting"></div>

## `formatting`

Used by output serialisers where string output is required.

For the formatting to be applied, the generated data must be applicable, and the `value` must be:

* a string recognised by Java's [`String.format` method](https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html)
* appropriate for the data type of `field`
* not `null` (formatting will not be applied for null values)

Formatting will not be applied if not applicable to the field's value.

Note that currently integer datatypes must be formatted as if they were decimals. For example to format a integer field to be used as part of a string a `value` like `"number: %.0f"` should be used. See [this](https://github.com/finos/datahelix/tree/master/examples/formatting) example to see another example of formatting integers as well as examples of formatting other datatypes.

This is an optional property of the field object and will default to use no formatting.

<div id="fields-unique"></div>

## `unique`

Sets the field as unique. Unique fields can not be used within [grammatical constraints](#Grammatical-Constraints).

 This is an optional property of the field object and will default to false.


# Data Types

## Integer/Decimal

Within a profile, users can specify two numeric data types: integer and decimal. Under the hood both of these data types are considered numeric from a point of generation but the integer type enforces a granularity of 1, see below for more information on granularity.

Decimals and integers have a maximum value of 1E20, and a minimum value of -1E20.

In profile files, numbers must be expressed as JSON numbers, without quotation marks.

### Numeric granularity

The granularity of a numeric field is a measure of how small the distinctions in that field can be; it is the smallest positive number of which every valid value is a multiple. For instance:

- if a numeric field has a granularity of 1, it can only be satisfied with multiples of 1; the integer data type adds this constraint by default
- if a decimal field has a granularity of 0.1, it can be satisfied by (for example) 1, 2, 1.1 or 1.2, but not 1.11 or 1.12

Granularities must be powers of ten less than or equal to zero (1, 0.1, 0.01, etc). Note that it is possible to specify these granularities in scientific format eg 1E-10, 1E-15 where the _10_ and _15_ clearly distinguish that these numbers can have up to _10_ or _15_ decimal places respectively. Granularities outside these restrictions could be potentially useful (e.g. a granularity of 2 would permit only even numbers) but are not currently supported.

Decimal fields currently default to the maximum granularity of 1E-20 (0.00000000000000000001) which means that numbers can be produced with up to 20 decimal places. This numeric granularity also dictates the smallest possible step between two numeric values, for example the next biggest decimal than _10_ is _10.00000000000000000001_. A user is able to add a `granularTo` constraint for a decimal value with coarser granularity (1, 0.1, 0.01...1E-18, 1E-19) but no finer granularity than 1E-20 is allowed.

Note that granularity concerns which values are valid, not how they're presented. If the goal is to enforce a certain number of decimal places in text output, the `formattedAs` operator is required. See: [What's the difference between formattedAs and granularTo?](user/FrequentlyAskedQuestions.md#whats-the-difference-between-formattedas-and-granularto)

## Strings

Strings are sequences of unicode characters with a maximum length of 1000 characters. Currently, only basic latin characters (unicode 002c - 007e) are supported.

## DateTime

DateTimes represent specific moments in time, and are specified in profiles through specialised strings:

```javascript
"2000-01-01T09:00:00.000"
```

The format is a subset of [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601); the date and time must be fully specified as above,
with precisely 3 digits of sub-second precision, plus an optional offset specifier of "Z". All datetimes are treated as UTC.

DateTimes can be in the range `0001-01-01T00:00:00.000Z` to `9999-12-31T23:59:59.999Z`
that is **_`midnight on the 1st January 0001`_** to **_`1 millisecond to midnight on the 31 December 9999`_**.

### DateTime granularity

The granularity of a DateTime field is a measure of how small the distinctions in that field can be; it is the smallest positive unit of which every valid value is a multiple. For instance:

- if a DateTime field has a granularity of years, it can only be satisfied by dates that are complete years (e.g. `2018-01-01T00:00:00.000Z`)

Granularities must be one of the units: millis, seconds, minutes, hours, days, months, years.

DateTime fields currently default to the most precise granularity of milliseconds. A user is able to add a `granularTo` constraint for a DateTime value with coarser granularity (seconds, minutes...years) but no finer granularity than milliseconds is currently allowed.

Note that granularity concerns which values are valid, not how they're presented. All values will be output with the full format defined by [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601), so that a value granular to years will still be output as (e.g.) `0001-01-01T00:00:00.000Z`, rather than `0001` or `0001-01-01`.

## Date
The date type can be used as a shorthand to create a [datetime](#DateTime) with a granularity and formatting of days. Dates should be specified in profiles as:

```javascript
"2001-01-01"
```
# Predicate constraints

## Theory

* A **predicate constraint** defines any given value as being _valid_ or _invalid_
* The **universal set** contains all values that can be generated (`null`, any string, any date, any number, etc)
* The **denotation** of a constraint is the subset of the universal set that it defines as valid

See [set restriction and generation](user/SetRestrictionAndGeneration.md) for an in depth explanation of how the constraints are merged and data generated from them.

If no constraints are defined over a field, then it can accept any member of the universal set. Each constraint added to that field progressively limits the universal set.

The [grammatical `not` constraint](#Grammatical-Constraints) inverts a constraint's denotation; in other words, it produces the complement of the constraint's denotation and the universal set.

## General constraints

<div id="predicate-equalto"></div>

### `equalTo` _(field, value)_  

```javascript
{ "field": "type", "equalTo": "X_092" }
OR
{ "field": "type", "equalTo": 23 }
OR
{ "field": "type", "equalTo": "2001-02-03T04:05:06.007" }
```

Is satisfied if `field`'s value is equal to `value`

<div id="predicate-inset"></div>

### `inSet` _(field, values)_

```javascript
{ "field": "type", "inSet": [ "X_092", "2001-02-03T04:05:06.007" ] }
```

Is satisfied if `field`'s value is in the set `values`

Alternatively, sets can be populated from files.

```javascript
{ "field": "country", "inSet": "countries.csv" }
```

Populates a set from the new-line delimited file (with suffix `.csv`), where each line represents a string value to load.
The file should be location in the same directory as the jar, or in the directory explicitly specified using the command line argument `--set-from-file-directory`, and the name should match the `value` with `.csv` appended.
Alternatively an absolute path can be used which does not have any relation to the jar location.
In the above example, this would be `countries.csv`.

Example `countries.csv` excerpt:
```javascript
...
England
Wales
Scotland
...
```

Additionally, weights can be included in the source file, which will then weight each element proportionally to its weight.
Example `countries_weighted.csv` excerpt:
```javascript
...
England, 2
Wales, 1
Scotland, 3
...
```

After loading the set from the file, this constraint behaves identically to the [inSet](#predicate-inset) constraint. This includes its behaviour when negated or violated.

<div id="predicate-inmap"></div>

### `inMap` _(field, file, key)_

```javascript
{
    "field": "country",
    "inMap": "countries.csv",
    "key": "Country"
}
```

Is satisfied if `field`'s value is in the map with the key `Country`.

For each field using the same map when one value is picked from the map all fields will use the same row.

It populates the map from a new-line delimited file (with suffix `.csv`), where each line represents a value to load. A header is required in the file to identify which column is related to a key.

The file should be location in the same directory as the jar, or in the directory explicitly specified using the command line argument `--set-from-file-directory`, and the name should match the `value` with `.csv` appended.
Alternatively an absolute path can be used which does not have any relation to the jar location.
In the above example, this would be `countries.csv`.

Example `countries.csv` excerpt:
```javascript
Country, Capital
England, London
Wales, Cardiff
Scotland, Edinburgh
...
```

<div id="predicate-null"></div>

### `null` _(field)_

```javascript
{ "field": "price", "isNull" : true }
```

Is satisfied if `field` is null or absent.

## String constraints

<div id="predicate-matchingregex"></div>

### `matchingRegex` _(field, value)_

```javascript
{ "field": "name", "matchingRegex": "[a-z]{0, 10}" }
```

Is satisfied if `field` is a string matching the regular expression expressed in `value`. The regular expression must match the entire string in `field`, start and end anchors `^` & `$` are ignored.

The following non-capturing groups are unsupported:
- Negative look ahead/behind, e.g. `(?!xxx)` and `(?<!xxx)`
- Positive look ahead/behind, e.g. `(?=xxx)` and `(?<=xxx)`

<div id="predicate-containingregex"></div>

### `containingRegex` _(field, value)_

```javascript
{ "field": "name", "containingRegex": "[a-z]{0, 10}" }
```

Is satisfied if `field` is a string containing the regular expression expressed in `value`. Using both start and end anchors `^` & `$` make the constraint behave like `matchingRegex`.

The following non-capturing groups are unsupported:
- Negative look ahead/behind, e.g. `(?!xxx)` and `(?<!xxx)`
- Positive look ahead/behind, e.g. `(?=xxx)` and `(?<=xxx)`

<div id="predicate-oflength"></div>

### `ofLength` _(field, value)_

```javascript
{ "field": "name", "ofLength": 5 }
```

Is satisfied if `field` is a string whose length exactly matches `value`, must be a whole number between `0` and `1000`.

<div id="predicate-longerthan"></div>

### `longerThan` _(field, value)_

```javascript
{ "field": "name", "longerThan": 3 }
```

Is satisfied if `field` is a string with length greater than `value`, must be a whole number between `-1` and `999`.

<div id="predicate-shorterthan"></div>

### `shorterThan` _(field, value)_

```javascript
{ "field": "name", "shorterThan": 3 }
```

Is satisfied if `field` is a string with length less than `value`, must be a whole number between `1` and `1001`.   

## Integer/Decimal constraints

<div id="predicate-greaterthan"></div>

### `greaterThan` _(field, value)_

```javascript
{ "field": "price", "greaterThan": 0 }
```

Is satisfied if `field` is a number greater than `value`.

<div id="predicate-greaterthanorequalto"></div>

### `greaterThanOrEqualTo` _(field, value)_

```javascript
{ "field": "price", "greaterThanOrEqualTo": 0 }
```

Is satisfied if `field` is a number greater than or equal to `value`.

<div id="predicate-lessthan"></div>

### `lessThan` _(field, value)_

```javascript
{ "field": "price", "lessThan": 0 }
```

Is satisfied if `field` is a number less than `value`.

<div id="predicate-lessthanorequalto"></div>

### `lessThanOrEqualTo` _(field, value)_

```javascript
{ "field": "price", "lessThanOrEqualTo": 0 }
```

Is satisfied if `field` is a number less than or equal to `value`.

<div id="predicate-granularto"></div>

### `granularTo` _(field, value)_

```javascript
{ "field": "price", "granularTo": 0.1 }
```

Is satisfied if `field` has at least the [granularity](#Numeric-granularity) specified in `value`.

## DateTime constraints
All dates must be in format `yyyy-MM-ddTHH:mm:ss.SSS` and the Z suffix can be included, but is not required. All datetimes are treated as UTC whether the Z suffix is included or not.

Example: `2001-02-03T04:05:06.007`

<div id="predicate-after"></div>

### `after` _(field, value)_

```javascript
{ "field": "date", "after": "2018-09-01T00:00:00.000" }
```

Is satisfied if `field` is a datetime occurring after `value`.

<div id="predicate-afterorat"></div>

### `afterOrAt` _(field, value)_

```javascript
{ "field": "date", "afterOrAt": "2018-09-01T00:00:00.000" }
```

Is satisfied if `field` is a datetime occurring after or simultaneously with `value`.

<div id="predicate-before"></div>

### `before` _(field, value)_

```javascript
{ "field": "date", "before": "2018-09-01T00:00:00.000" }
```

Is satisfied if `field` is a datetime occurring before `value`.

<div id="predicate-beforeorat"></div>

### `beforeOrAt` _(field, value)_

```javascript
{ "field": "date", "beforeOrAt": "2018-09-01T00:00:00.000" }
```

Is satisfied if `field` is a datetime occurring before or simultaneously with `value`.

<div id="predicate-granularto-datetime"></div>

### `granularTo` _(field, value)_

```javascript
{ "field": "date", "granularTo": "days" }
```

Is satisfied if `field` has at least the [granularity](#DateTime-granularity) specified in `value`.

<div id="otherfield-constraints"></div>

## Dependant field constraints

<div id="predicate-otherfield"></div>

### `otherField`
allows a date field to be dependant on the output of another date field

```javascript
{ "field": "laterDateField","after": "previousDateField" }
```

supported operators are currently
"after", "afterOrAt", "before", "beforeOrAt", "equalTo"

<div id="predicate-offset"></div>

### `offset`
Allows a dependant date to always be a certain offset away from another date

```javascript
{ "field": "threeDaysAfterField","equalToField": "previousDateField", "offset": 3, "offsetUnit": "days" }
```

# Grammatical constraints
<div id="Grammatical-constraints"></div>

**Grammatical constraints** combine or modify other constraints. They are fully recursive; any grammatical constraint is a valid input to any other grammatical constraint.

See [set restriction and generation](user/SetRestrictionAndGeneration.md) for an in depth explanation of how the constraints are merged and data generated from them.

## `not`

```javascript
{ "not": { "field": "foo", "equalTo": "bar" } }
```

Wraps a constraint. Is satisfied if, and only if, its inner constraint is _not_ satisfied.

## `anyOf`

```javascript
{ "anyOf": [
    { "field": "foo", "isNull": true },
    { "field": "foo", "equalTo": 0 }
]}
```

Contains a number of sub-constraints. Is satisfied if any of the inner constraints are satisfied.

## `allOf`

```javascript
{ "allOf": [
    { "field": "foo", "greaterThan": 15 },
    { "field": "foo", "lessThan": 100 }
]}
```

Contains a number of sub-constraints. Is satisfied if all of the inner constraints are satisfied.

## `if`

```javascript
{
    "if":   { "field": "foo", "lessThan": 100 },
    "then": { "field": "bar", "greaterThan": 0 },
    "else": { "field": "bar", "equalTo": "N/A" }
}
```

Is satisfied if either:

- Both the `if` and `then` constraints are satisfied
- The `if` constraint is not satisfied, and the `else` constraint is

While it's not prohibited, wrapping conditional constraints in any other kind of constraint (eg, a `not`) may cause unintuitive results.


# Running a Profile
<div id="Running-a-Profile"></div>

Profiles can be run against a jar using the command line.

## Command Line Arguments
<div id="Command-Line-Arguments"></div>

Currently the only mode fully supported by the data helix is generate mode. An example command would be something like

`java -jar generator.jar generate --max-rows=100 --replace --profile-file=profile.json --output-path=output.csv`

### Command Line Arguments for Generate Mode
<div id="Command-Line-Arguments-for-Generate-Mode"></div>
Option switches are case-sensitive, arguments are case-insensitive

* `--profile-file=<path>` (or `-p <path>`)
    * Path to the input profile file.
* `--output-path=<path>` (or `-o <path>`)
    * Path to the output file.  If not specified, output will be to standard output.
* `--replace`
    * Overwrite/replace existing output files. Defaults to false.
* `-n <rows>` or `--max-rows <rows>`
   * Emit at most `<rows>` rows to the output file, if not specified will limit to 10,000,000 rows.
   * Mandatory in `RANDOM` mode.
* `--generation-type`
    * Determines the type of (data generation)[Link] performed. Supported options are `FULL_SEQUENTIAL` and `RANDOM`(default).
* `--combination-strategy`
    * Determines the type of combination strategy used in full sequential mode. Supported options are `MINIMAL`(default), `EXHAUSTIVE` and `PINNING`.
* `--output-format`
    * Determines the output format. Supported options are `csv`(default) and `json`.
* `--ndjson`
    * When combined with the `--output-format=json` flag sets the output format to [ndjson](http://ndjson.org/). Defaults to true if the `--output-flag` is not set and to false if it is set.
* `--visualiser-level`
    * Determines level of visualisation using.  Supported options are `OFF` (default), `STANDARD` and `DETAILED`
* `--visualiser-output-folder`
    * The path to the folder to write the generated visualiser files to (defaults to current directory (`.`).  Its only used if `visualiser-level` != `OFF`.

By default the generator will report how much data has been generated over time, the other options are below:
* `--verbose`
    * Will report in-depth detail of data generation
* `--quiet`
    * Will disable velocity reporting

`--quiet` will be ignored if `--verbose` is supplied.

## Generation Strategies
<div id="Generation Strategies"></div>
The generation mode can be specified by the `--generation-type` flag.

The generator supports the following data generation types

* Random (_default_)
* Full Sequential

### Random Mode
<div id="Random Mode"></div>
Generate some random data that abides by the given set of constraints.

Examples:

| Constraint | Emitted valid data |
| ---- | ---- | ---- |
| `Field 1 > 10 AND Field 1 < 20` | _(any values > 10 & < 20)_ |
| `Field 1 in set [A, B, C]` | _(A, B or C in any order, repeated as needed)_ |

Notes:
- Random generation of data is infinite and is limited to 1000 by default, use `--max-rows` to enable generation of more data.

### Full Sequential Mode
<div id="Full-Sequential-Mode"></div>
Generate all data that can be generated in order from lowest to highest.

Examples:

| Constraint | Emitted valid data |
| ---- | ---- | ---- |
| `Field 1 > 0 AND Field 1 < 5` | _(null, 1, 2, 3, 4)_ |
| `Field 1 in set [A, B, C]` | _(null, A, B, C)_ |

* Note that null will only be produced depending on the [properties](https://github.com/finos/datahelix/blob/master/docs/UserGuide.md#Fields) of Field 1.

#### Combination Strategies
<div id="Combination-Strategies"></div>
There are a few different combination strategies which can be used in **full sequential mode** with minimal being the default. In modes other than full sequential, combination strategy will have no effect.

It is simplest to see how the different combination strategies work by look at the effect on a simple example profile. The following [profile](https://github.com/finos/datahelix/tree/master/examples/multiple-fields/profile.json) contains two fields:
  * field1 - has values in set [ "A", "B" ]
  * field2 - has values in set [ 1, 2, 3 ]  

##### Minimal
<div id="Minimal"></div>

The minimal strategy outputs the minimum data required to exemplify each value at least once. Per the example, the output would be:

* "A",1
* "B",2
* "B",3

Note that minimal is the default combination strategy.

##### Exhaustive
<div id="Exhaustive"></div>

The exhaustive strategy outputs all possible combinations. Given the fields as defined above, possible outputs would be:

* "A",1
* "B",1
* "A",2
* "B",2
* "A",3
* "B",3


##### Pinning
<div id="Pinning"></div>

The pinning strategy establishes a baseline for each field (generally by picking the first available value for that field) and then creates outputs such that either:

* All values equal the baseline for the respective field
* All values except one equal the baseline for the respective field

To generate these outputs, we first output the first case (all values from baseline) and then iterate through each field, F, fixing all other fields at their baseline and generating the full range of values for F. For the example, the output would be:

* "A",1
* "A",2
* "A",3
* "B",1

# Visualising Decision Trees
<div id="Visualising-Decision-Trees"></div>

_This is an alpha feature. Please do not rely on it. If you find issues with it, please [report them](https://github.com/finos/datahelix/issues)._ 

This feature generates a <a href=https://en.wikipedia.org/wiki/DOT_(graph_description_language)>DOT</a> compliant representation of the decision tree, 
for manual inspection, in the form of a DOT formatted file.

If use the `--visualiser-level` and `--visualiser-output-folder` command line options when generating data then 
you can get visualisations of the decision tree outputted as graphs in DOT files. 

* See [Developer Guide](DeveloperGuide.md) for more information on the decision tree structure.
* See [Command Line Arguments for Generate Mode](#Command-Line-Arguments-for-Generate-Mode) for more information on the command line arguments.

The visualiser levels can have the following values:
* OFF - the default and it means no graphs outputted 
* STANDARD - it means graphs of the decision tree outputted after each pre-walking stage is done
* DETAILED - it means the standard decision trees and the various decision trees created during walking stage are outputted

## How to Read the DOT Files produced

* You may read a DOT file with any text editor
* You can also use this representation with a visualiser such as [Graphviz](https://www.graphviz.org/).

    There may be other visualisers that are suitable to use. The requirements for a visualiser are known (currently) as:
    - DOT files are encoded with UTF-8, visualisers must support this encoding.
    - DOT files can include HTML encoded entities, visualisers should support this feature.

