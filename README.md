# hachiba

Hachiba (live version at http://practicalhuman.org) is an image and discussion board built with usability in mind.
Boards can be created and immediately posted to, creating a new thread.
Threads get unique page identities (practicalhuman.org/boardName/a37qR29/ looks like one, for example).
And, it's super fast!
The basic gist is fully functional and as improvements are approved more features shall be incorporated.

## updates

5 July 2018: added use of duratoms (instead of plain ol' atoms) so now all is saved to disk ( in .sova files)


## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Install

Clone this project using git clone and pointing to the .git file for this repo, or download this repo and navigate there via command line.

When you have Leiningen and Clojure installed you can type in `lein deps` to have leiningen fetch all the dependencies.

_However, you can also just skip to `lein ring server` and it'll do all that for you._





## Running

To start a web server for the application, run:

    lein ring server

or alternatively, without a browser launch:

    lein ring server-headless


One must create the directory resources/public/uploads (e.g. `mkdir resources/public/uploads/`) so that the file writer can save images there, otherwise you will get null pointer exceptions on uploading images.

(If you know how to make it so that your Clojure project will also automatically create a new directory when being initialized for the first time some place new, please let me know.)




## What is this?

In the spirit of simple, fast, and intuitive image and discussion boards of the mighty nineties and noughts, the current incarnation of ph was born. *thunder claps and lightening bursts over the horizon*




## Rejoice

As of 5 July 2018 this message board will save all posts, threads, and boards to disk in files ending with .sova


## Why did you make this?

To prove I am indeed a master ninja.

## How does that prove you are a master ninja?

This project was created in 8 years and 72 hours.  Eight years because that's how long I've been contemplating message boards and forums deeply and significantly, and seventy-two hours because the main Hachiba "engine" came together in 3 consecutive nights.

In Clojureland, and with some ninja mastery, everything is functioning smoothly and compactly, keeping a tiny footprint.

Without all the extra weight, this web server can accommodate many connections, very rapidly, and still look like a complete and compliant web page to Google Crawler's "eyes."


## Not really sure what you said just now

Yeah don't worry about it go listen to some music

## Okay cool bai

Thanks for checking this out :D


## License

Copyright © 2018 Practical Human (http://practicalhuman.org)
Personal, non-commercial use of this software is permitted indefinitely and modifications to this software when used for personal, non-commercial use, are permitted and encouraged.  For commercially intended use, or use that will accrue revenue by any means not limited to advertising royalties sales and or trade facilitation, please contact the software publisher at `studio@nonforum.com` to discuss options on pricing.
Unless otherwise stated, all rights reserved.
This source code enjoys open access, for educational and personal, non-commercial use.
