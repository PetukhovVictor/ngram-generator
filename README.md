# ngram-generator
N-gram generation by tree or list

## Description

The program allows for a set of files with trees or lists to generate n-grams (unigrams, bigrams and 3-grams).

### N-grams generation by tree

Bulding of n-grams on the tree occurs via depth-first search and memorizing the path of the walk. On the basis of the stored path, a set of n-grams is built at each node in which it contained.

Example of n-grams generation by tree:
![Tree n-grams generation](https://github.com/PetukhovVictor/ngram-generator/raw/master/images/tree-ngrams.png)

## Program use


### Program arguments

* `-i` or `--input`: path to folder with structure JSON files (trees or lists);
* `-o` or `--output`: path to folder, in which will be written files with generated n-grams;
* `--tree`: whether to generate by tree;
* `--list`: whether to generate by list.

### How to run

To run program you must run `main` function in `main.kt`, not forgetting to set the program arguments.

Also you can run jar file (you can download from the [release assets](https://github.com/PetukhovVictor/ngram-generator/releases)):
```
java -jar ./ngram-generator-0.1.jar -i ./trees -o ./ngrams --tree
```
