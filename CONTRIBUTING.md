## Contributing

First off, thank you for considering contributing to TelepathDB. It's people like you that make TelepathDB such a great system.

### 1. Where do I go from here?

If you've noticed a bug or have a question, [search the issue tracker](https://github.com/giedomak/TelepathDB/issues?utf8=%E2%9C%93&q=is%3Aopen) to see if someone else in the community has already created a ticket. If not, go ahead and [make one](https://github.com/giedomak/TelepathDB/issues/new)!

### 2. Fork & create a branch

If this is something you think you can fix, then [fork TelepathDB](https://help.github.com/articles/fork-a-repo) and create a branch with a descriptive name.

A good branch name would be (where issue #325 is the ticket you're working on):

```sh
git checkout -b 325-add-japanese-translations
```

### 3. Get the test suite running

Make sure you're using Java `1.8` and you have installed at least version `3.5` of Maven.

Now you should be able to run the entire test suite using:

```sh
mvn clean test
```

### 4. Implement your fix or feature

At this point, you're ready to make your changes! Feel free to ask for help; everyone is a beginner at first :smile_cat:.

Available getting started guides:

- [Implementing a new physical operator](https://github.com/giedomak/TelepathDB/tree/master/src/main/java/com/github/giedomak/telepathdb/physicaloperators)

### 5. Make a Pull Request

At this point, you should switch back to your master branch and make sure it's
up to date with Active Admin's master branch:

```sh
git remote add upstream git@github.com:giedomak/TelepathDB.git
git checkout master
git pull upstream master
```

Then update your feature branch from your local copy of master, and push it!

```sh
git checkout 325-add-japanese-translations
git rebase master
git push --set-upstream origin 325-add-japanese-translations
```

Finally, go to GitHub and [make a Pull Request](https://help.github.com/articles/creating-a-pull-request) :D

Travis CI will run the test suite and other integrations like codeclimate will analyse the code quality. 
We care about quality, so your PR won't be merged until the tests pass and the quality of the PR is good enough.
