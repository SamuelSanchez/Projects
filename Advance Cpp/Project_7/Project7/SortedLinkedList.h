#ifndef SORTEDLINKEDLIST_H
#define SORTEDLINKEDLIST_H

#include "Node.h"

template <class K, typename V>
class SortedLinkedList
{
   private:
      Node<K,V> * head;	//Dumby node
      Node<K,V> * tail;
      size_t size;

      //Copy the entire list to this object
      void copyList(const SortedLinkedList&);
   public:
      //Default Constructor
      SortedLinkedList();
      //Copy Constructor
      SortedLinkedList(const SortedLinkedList&);
      //Assignment operator
      SortedLinkedList& operator= (const SortedLinkedList&);
      //Destructor
      ~SortedLinkedList();

      //Returns the size of this linkedlist - not counting the head
      int getSize() const;
      //Returns true if the list is empty
      bool isEmpty() const;
      //Clears the entire list
      void clear();
      //Insert key and value
      void insert(const K&, const V&);
	  //Returns true if the elements exists, false otherwise
	  bool exists(const K&) const;
	  //Returns the elements if it exists, NULL otherwise
	  V* find(const K&) const;
	  
	  Node<K,V>* begin() const{
		return head->next;
	  }
	  
	  template <class _K,class _V>
      friend std::ostream& operator<< (std::ostream& os, const SortedLinkedList<_K,_V>& node);
};
#endif  /* SORTEDLINKEDLIST_H */

/* SortedLinkedList.cpp */
//Copy the entire list to this list
template <class K, typename V>
void SortedLinkedList<K,V>::copyList(const SortedLinkedList<K,V>& list)
{
    for(Node<K,V> * curr = list.head->next; curr != NULL; curr = curr->next)
    {
        Node<K,V> * newNode = new Node<K,V>(curr->key, curr->value);
        tail->next = newNode;
        tail = newNode;
    }
}

template <class K, typename V>
inline SortedLinkedList<K,V>::SortedLinkedList():size(0)
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : Empty Constructor" << std::endl;
	#endif	
	head = new Node<K,V>();  //Dumby node
	tail = head;
}

template <class K, typename V>
inline SortedLinkedList<K,V>::SortedLinkedList(const SortedLinkedList<K,V>& list):size(list.size)
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : Copy Constructor" << std::endl;
	#endif
	head = new Node<K,V>();  //Dumby node
    tail = head;
	//Copy the list
	copyList(list);
}

template <class K, typename V>
inline SortedLinkedList<K,V>& SortedLinkedList<K,V>::operator= (const SortedLinkedList<K,V>& list)
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : Assignment Operator" << std::endl;
	#endif
	if(this != &list){
		//Empty this list
		clear();
		size = list.size;
		//Copy the list
		copyList(list);
	}
	return *this;
}

template <class K, typename V>
inline SortedLinkedList<K,V>::~SortedLinkedList()
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : ~Destructor" << std::endl;
	#endif
	clear();
	delete head;
}

template <class K, typename V>
inline int SortedLinkedList<K,V>::getSize() const
{
   return size;
}

template <class K, typename V>
inline bool SortedLinkedList<K,V>::isEmpty() const
{
   return size == 0;
}

template <class K, typename V>
void SortedLinkedList<K,V>::clear()
{
    #ifdef DEBUG
		std::cout << "Size : " << size << std::endl;
    #endif

    Node<K,V> * current = head->next;
    while(current != NULL)
    {
        head->next = current->next;
        delete current;
        size--;
        current = head->next;
    }
    tail = head;

    #ifdef DEBUG
		std::cout << "Size : " << size << std::endl;
    #endif
}

template <class K, typename V>
void SortedLinkedList<K,V>::insert(const K& key, const V& value)
{
	//Insert in ascending order
	Node<K,V> * newNode;
    Node<K,V> * prev = head;
    Node<K,V> * curr = head->next;

    //If the list is empty, insert the first node
    if(curr == NULL)
    {
        newNode = new Node<K,V>(key,value);
        head->next = newNode;
        tail = newNode;
        size++;
    }
    else
    {
        while(curr != NULL)
        {
            //If this key is lower than the current, the preppend
            if(key < curr->key)
            {
                newNode = new Node<K,V>(key,value);
                prev->next = newNode;
                newNode->next = curr;

                if(curr->next == NULL){
                    tail = curr;
                }
                size++;
                break;
            }
            //If they have the same key, then replace
            else if(key == curr->key)
            {
				curr->value = value;
                break;
            }
            //If the node is bigger continue iterating
            else
            {
                prev = prev->next; //prev = current;
                curr = curr->next;
                //If this is the largest degree append last
                if(curr == NULL)
                {
                    newNode = new Node<K,V>(key,value);
                    prev->next = newNode;
                    tail = newNode;
                    size++;
                    break; //Don't need to break bcuz for the next iteration it will evaluate to false
                }
            }
        }
    }
}

template <class K, typename V>
bool SortedLinkedList<K,V>::exists(const K& key) const
{
	for(Node<K,V> * curr = head->next; curr != NULL; curr = curr->next){
		if(key == curr->key){
			return true;
		}
	}
	return false;
}

template <class K, typename V>
V* SortedLinkedList<K,V>::find(const K& key) const
{
	for(Node<K,V> * curr = head->next; curr != NULL; curr = curr->next){
		if(key == curr->key){
			return &curr->value;
		}
	}
	return NULL;
}

template <class K, typename V>
std::ostream& operator<< (std::ostream& os, const SortedLinkedList<K,V>& list)
{
    for(Node<K,V> * current = list.head->next; current != NULL; current = current->next)
    {
		os << *current;
	}
	return os;
}