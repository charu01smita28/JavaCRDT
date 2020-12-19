# javaCRDT
2 implementations of conflict-free replicated data types (CRDT) in JAVA
  
## What is CRDT ?
A conflict-free replicated data type (CRDT) is a type of data structure that is used to achieve strong eventual consistency and monotonicity (ie, there are no rollbacks) across a set of nodes in a distributed system. It can be used in very specific cases to implement a distributed data store that does not require any form of synchronization to function.

## Implementations of CRDT 
### Last-Writer-Wins (LastWriterWinSet) Set
1. One common CRDT data structure is the Last-Writer-Wins (LWW) Element Set. This set stores only one instance of each element in the set, and associates it with a timestamp. 
The LWW Element Set CRDT can be implemented using two separate underlying simple sets: an Add set (ZA) and a Remove set (ZR), both of which store an entry consisting of the element and a timestamp, [e, t]. 
Operations which can be performed on it are : 
* ```java
      New() -> Z
  ```
  * Creates a new Add set ZA and a Remove set ZR or clears the sets if already existing
* ```java 
      Add(Z,e,t) -> t
  ```
  * When an element e is added to the CRDT set Z, the Add set ZA is modified as follows:
    *	If there is already an entry in ZA for e, its timestamp is set to t if t is more recent than the currently-stored timestamp.
    *	Otherwise, a new entry is inserted into ZA consisting of the element e and the given time t.
* ```java
      Remove(Z,e,t) -> t
  ```
  *	When an element e is removed from the CRDT set Z, the Remove set ZR is modified as follows:
    *	If there is already an entry in ZR for e, its timestamp is set to t if t is more recent than the currently-stored timestamp.
    *	Otherwise, a new entry is inserted into ZR consisting of the element e and the given time t.
* ```java
      Exists(Z,e) -> boolean
  ```
  *	It determines whether an element e is in the CRDT set Z by find the entry containing e in both ZA and ZR. 
    * If the timestamp of the entry from ZA is more recent than that of the entry from ZR, the element is in the set. 
    * If the element isn’t present, or the entry from ZR is newer than that from ZA, the element is not in the set.
* ```java
     Get(Z) -> [e]
  ```
  * It generates the contents of the CRDT set Z by scanning both ZA and ZR, selecting only those elements that are present in ZA without also being present in ZR, or where the timestamp for the element in ZA is newer than the timestamp for the element in ZR.

### Redis ZSET implementation (LastWriterWinRedisSet)
2. Redis doesn’t currently implement any CRDT types, it does provide a sorted set type called a ZSET that we can use to implement the LWW Element Set CRDT.
* A Redis ZSET stores member values alongside a “score”. The set is ordered by the score, but there can never be more than one instance of a given member in a single ZSET (that is, inserting a member “abc” with score 1 and then inserting another “abc” but with score 2 results in a single member, “abc”, with score 2 -- the score is simply updated).
* Used [Redisson client library ZSET](https://github.com/redisson/redisson/wiki/7.-Distributed-collections#75-scoredsortedset) for the implementing the CRDT Last Writer Wins Set.
* Same operations performed as above. But implementation is simpler as Redis ZSET already has several common characteristics with LWW Element Set.
    * **NOTE**:   To be able to successfully run the <code>LastWriterWinsRedisSetTest</code> tests, you need to have a local instance of 
                  Redis Server running [Redis](https://redis.io/download).   
