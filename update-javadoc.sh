#!/bin/bash
echo '--> generating new dokka:javadoc'
mvn dokka:javadoc
echo '--> cd into javadoc dir'
cd target/dokkaJavadoc/
echo '--> fetch the remote gh-pages branch'
git fetch --depth=1 javadoc gh-pages
echo '--> commit and merge our changes'
git add --all
git commit -m 'update javadoc'
git merge --no-edit -s ours remotes/javadoc/gh-pages
echo '--> push our changes'
git push javadoc master:gh-pages
echo '--> cd back to our project'
cd ../../
