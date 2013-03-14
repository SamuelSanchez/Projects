#ifndef PERMUTEDINDEX
#define PERMUTEDINDEX_H

#include "SortedLinkedList.h"

class PermutedIndex
{
   private:
      SortedLinkedList<std::string, SortedLinkedList<int,int> > dictionary;
   public:
      //Default Constructor
      PermutedIndex();
      //Copy Constructor
      PermutedIndex(const PermutedIndex&);
      //Assignment operator
      PermutedIndex& operator= (const PermutedIndex&);
      //Destructor
      ~PermutedIndex();

      void insert(const std::string&, const int&);
      //Overloading operators
      friend std::ostream& operator<< (std::ostream&, const PermutedIndex&);
};
#endif  /* PERMUTEDINDEX_H */

/* PermutedIndex.cpp */

inline PermutedIndex::PermutedIndex()
{
	#ifdef DEBUG
		std::cout << "PermutedIndex : Empty Constructor" << std::endl;
	#endif
	//dictionary = SortedLinkedList<std::string, SortedLinkedList<int,int> >();
}

inline PermutedIndex::PermutedIndex(const PermutedIndex& obj)
{
	#ifdef DEBUG
		std::cout << "PermutedIndex : Copy Constructor" << std::endl;
	#endif
	dictionary = obj.dictionary;
}

inline PermutedIndex& PermutedIndex::operator= (const PermutedIndex& obj)
{
	#ifdef DEBUG
		std::cout << "PermutedIndex : Assignment Operator" << std::endl;
	#endif
	if(this != &obj){
		dictionary.clear();
		dictionary = obj.dictionary;
	}
	return *this;
}

inline PermutedIndex::~PermutedIndex()
{
	#ifdef DEBUG
		std::cout << "PermutedIndex : ~Destructor" << std::endl;
	#endif
	dictionary.clear();
}

void PermutedIndex::insert(const std::string& word, const int& line)
{
	#ifdef DEBUG
		std::cout << "PermutedIndex : insert" << std::endl;
	#endif
	//Check if it's an existing word
	if( dictionary.exists( word ) ){
		SortedLinkedList<int,int> * it = dictionary.find( word );
		
		//Check if the word belongs to an existing line
		if( it->exists( line ) ){
			int * ele = it->find(line);
			*ele = *ele + 1;	//Dereference in order to increase value
		}//New line
		else{
			it->insert( line, 1 );	
		}
	}
	else{//Insert the new workd with the line just found
		SortedLinkedList<int,int> counter;
		counter.insert( line, 1 );
		dictionary.insert( word, counter );
	}
}

std::ostream& operator<< (std::ostream& os, const PermutedIndex& obj)
{
	int mapSize = 0;
    for(Node<std::string, SortedLinkedList<int,int> > * it = obj.dictionary.begin(); 
		it != NULL; it = it->next)
    {
		os << it->key << ":";
		mapSize = it->value.getSize();	//Get size of the map
		int index = 1; 					//Start at one
		for(Node<int,int> * ele = it->value.begin();
			ele != NULL; ele = ele->next)
		{
			os << " " << ele->key;
			//If there are more elements in the same line then display
			if(ele->value > 1){
				os << "(" << ele->value << ")";
			}
			//Add the Comma
			if( index < mapSize ){
				os << ",";
			}
			
			index++;
		}
		os << std::endl;
	}
	return os;
}