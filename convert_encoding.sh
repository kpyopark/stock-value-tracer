#!/bin/sh

wrongencodings=`mvn compile 2> /dev/null  | grep encoding | awk '{ print $2 }' | awk -F ':' '{ print $1 }' | sort | uniq | grep java`

for wrongencoding in ${wrongencodings}
do
iconv -f cp949 -t utf8 ${wrongencoding} > ${wrongencoding}.utf8
rm -i ${wrongencoding}
mv ${wrongencoding}.utf8 ${wrongencoding}
done

