I wrote a tester to repeatedly create and delete the git directory with all of its contents. I made sure my cleanup method checks if the directory with all necessary files exists before trying to delete it. My implementation is clear and checks for edge cases.

I added a SHA1 hash method that will be used for my BLOBS.