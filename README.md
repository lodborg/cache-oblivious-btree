# Cache-Oblivious B-Trees

__*Currently under construction*__

Eventually, here will emerge an implementation of a Cache-Oblivious B-Tree, that performs efficiently without prior knowledge of the memory hierarchy. Essentially, the main idea is to build a van Emde Boas layout on top of a Packed Memory Array. The result is a binary search algorithm that takes advantage of cache locality and minimizes the amount of external memory reads.

Sit back, enjoy a cup of coffee and maybe have a look at some links on the topic:
* The papers that sparked my interest in the topic: [here](http://erikdemaine.org/papers/CacheObliviousBTrees_SICOMP/paper.pdf) and [here](http://erikdemaine.org/papers/FOCS2000b/paper.pdf)
* A paper on [Adaptive Packed Memory Arrays](https://www3.cs.stonybrook.edu/~bender/newpub/BenderHu07-TODS.pdf) (or packed memory arrays on steroids)
* Or an [MIT lecture](https://www.youtube.com/watch?v=V3omVLzI0WE) on the topic given by the one and only Erik Demaine. Highly recommended! This guy is a legend.