Sudoku-Puzzles
==============

Java Framework to create and play Sudoku-like Puzzles

Sudoku
======

Can load and save a Sudoku puzzle from a flat text file (csv), for example

    0,0,0,2,0,4,8,1,0
    0,4,0,0,0,8,2,6,3
    3,0,0,1,6,0,0,0,4
    1,0,0,0,4,0,5,8,0
    6,3,5,8,2,0,0,0,7
    2,0,0,5,9,0,1,0,0
    9,1,0,7,0,0,0,4,0
    0,0,0,6,8,0,7,0,1
    8,0,0,4,0,3,0,5,0

CLI interface for playing and testing in a terminal.

Commands are now:

    Command (h,q,p,d,m,b,c,u,s,l) : h
    h : help
    p : put
    d : delete
    m : markUp
    b : bruteForce
    c : create
    u : unique
    q : quit
    s : save
    l : load

    (p) puts a value. Asks for row, column and value. 
    (c) generates new puzzles. Puzzles are not graded at the moment.
    (u) proves that a puzzle has a unique solution


Rudimentary Swing interface

Samurai
=======

Framework created, cannot read or write yet (or play for that matter).

Requirements
============

JDK 1.8 
argparse4j 0.4.3
junit 4.11 (for test cases)
