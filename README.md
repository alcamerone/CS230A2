# CS230A2
My submission for the second assignment of the course COMPSCI 230 at the University of Auckland.

Two simple Java programs meant to demonstrate the efficiency of multi-threading in Java.

The first, SOD.java, takes a matrix of numbers and computes the sum of the divisors of each of the numbers individually, then the sum of the divisors of each row of sums of divisors, and then the sum of the divisors of all sums of divisors for each row added together. The task is done serially and in parallel to demonstrate the increased efficiency of multithreading.

An example run (after compiling):

    java SOD largetest.txt

Which should produce the result:

    [1] Seqsum lines=746
    [1] Seqsum res=1604975456 secs= 36.591
    [1] ParSum lines=746 procs=8
    [1] ParSum threads=[16, 17, 18, 11, 12, 13, 14, 15]
    [1] ParSum res=1604975456 secs= 23.104

The second, WEB.java, takes a list of URLs and performs a GET request to each URL, then prints the indicated line number and the final line of the HTML response. Like SOD.java, it does this task sequentially and in parallel.

An example run:

    java WEB test.txt

Which should produce the result:

    [1] SeqFetch urls=3

    [1] 0   :https://www.cs.auckland.ac.nz/en.html
    [1] 0.6 :
    [1] 0.2785      :</html>
    [1] 1   :http://www.science.auckland.ac.nz/en.html
    [1] 1.6 :
    [1] 1.3408      :</html>
    [1] 2   :http://www.auckland.ac.nz/
    [1] 2.6 :<hr>
    [1] 2.9 :</body></html>
    
    [1] SeqFetch secs=0.909
    
    [1] ParFetch urls=3 procs=8
    
    [14] 1  :http://www.science.auckland.ac.nz/en.html
    [15] 2  :http://www.auckland.ac.nz/
    [13] 0  :https://www.cs.auckland.ac.nz/en.html
    [15] 2.6        :<hr>
    [15] 2.9        :</body></html>
    [14] 1.6        :
    [13] 0.6        :
    [13] 0.2785     :</html>
    [14] 1.3408     :</html>
    
    [1] ParFetch secs=0.496

More details can be found in the file A2v3.pdf.
