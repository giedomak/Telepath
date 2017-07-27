#!/bin/bash
# https://vaadin.com/blog/-/blogs/host-your-javadoc-s-online-in-github
echo '--> generating new dokka:dokka'
mvn dokka:dokka
echo '--> cd into dokka dir'
cd target/dokka/
echo '--> init git'
git init
echo '--> add remote repo'
git remote add dokka git@github.com:giedomak/TelepathDB.git
echo '--> fetch the remote gh-pages branch'
git fetch --depth=1 dokka gh-pages
echo '--> commit and merge our changes'
git add --all
git commit -m 'update dokka'
git merge --no-edit -s ours --allow-unrelated-histories remotes/dokka/gh-pages
echo '--> push our changes'
git push dokka master:gh-pages
echo '--> cd back to our project'
cd ../../
