#ifndef SORTEDLINKEDLIST_H
#define SORTEDLINKEDLIST_H

class SortedLinkedList
{
   private:
	  struct node{
		int c, d;
		node(int _c, int _d){
			c = _c;
			d = _d;
		}
	  };
      std::map<int,int> canonicalForm;
	  std::list<node> originalForm;
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
      //Insert coefficient and exponent to the list
      void insert(const int&, const int&);
      //Overloading operators
      SortedLinkedList operator+ (const SortedLinkedList&);
      SortedLinkedList operator- (const SortedLinkedList&);
      SortedLinkedList operator* (const SortedLinkedList&);
      friend std::ostream& operator<< (std::ostream&, const SortedLinkedList&);
	  //Returns the contents of this list
	  std::string printOriginal() const;
	  std::string toString() const;
};
#endif  /* SORTEDLINKEDLIST_H */

/* SortedLinkedList.cpp */

inline SortedLinkedList::SortedLinkedList()
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : Empty Constructor" << std::endl;
	#endif
}

inline SortedLinkedList::SortedLinkedList(const SortedLinkedList& list)
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : Copy Constructor" << std::endl;
	#endif
	canonicalForm = list.canonicalForm;
	originalForm = list.originalForm;
}

inline SortedLinkedList& SortedLinkedList::operator= (const SortedLinkedList& list)
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : Assignment Operator" << std::endl;
	#endif
	if(this != &list){
		canonicalForm.clear();
		originalForm.clear();
		canonicalForm = list.canonicalForm;
		originalForm = list.originalForm;
	}
	return *this;
}

inline SortedLinkedList::~SortedLinkedList()
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : ~Destructor" << std::endl;
		//std::cout << (*this) << std::endl;
		std::cout << "-------------" << std::endl;
	#endif
	canonicalForm.clear();
	originalForm.clear();
}

inline int SortedLinkedList::getSize() const
{
   return canonicalForm.size();
}

inline bool SortedLinkedList::isEmpty() const
{
   return canonicalForm.empty();
}

void SortedLinkedList::clear()
{
    #ifdef DEBUG
    std::cout << "Size : " << size << std::endl;
    #endif
	canonicalForm.clear();
	originalForm.clear();
}

void SortedLinkedList::insert(const int& coefficient, const int& exponent)
{
	//Insert original Form
	node n(coefficient,exponent);
	originalForm.push_back(n);

    //Don't add 0 coefficients
    if(coefficient == 0) return;
    
	//Insert Canonical Form
	std::map<int,int>::iterator it;
	if(canonicalForm.count(exponent) > 0){
		it = canonicalForm.find(exponent);
		int exp = it->second;
		if( exp + coefficient == 0 ){
			canonicalForm.erase( it );
		}
		else{
			it->second = (exp + coefficient);
		}
	}
	else{
		canonicalForm.insert( std::pair<int,int>(exponent,coefficient) );
	}
}

SortedLinkedList SortedLinkedList::operator+ (const SortedLinkedList& list)
{
	SortedLinkedList temp(*this);
	std::map<int,int>::const_iterator it;
	for(it = list.canonicalForm.begin(); it != list.canonicalForm.end(); it++)
	{
		temp.insert(it->second, it->first);
	}
	return temp;
}

SortedLinkedList SortedLinkedList::operator- (const SortedLinkedList& list)
{
	SortedLinkedList temp(*this);
	std::map<int,int>::const_iterator it;
	for(it = list.canonicalForm.begin(); it != list.canonicalForm.end(); it++)
	{
		temp.insert(-1 * it->second, it->first);
	}
	return temp;
}

SortedLinkedList SortedLinkedList::operator* (const SortedLinkedList& list)
{
	SortedLinkedList total;
	std::map<int,int>::reverse_iterator it_1;
	std::map<int,int>::const_reverse_iterator it_2;
	for(it_1 = canonicalForm.rbegin(); it_1 != canonicalForm.rend(); it_1++)
	{
		for(it_2 = list.canonicalForm.rbegin(); it_2 != list.canonicalForm.rend(); it_2++)
		{
			//None of the list have zero coefficients
			//It's taken cared of in the insert method
			//If the left hand side is zero coefficient don't loop
			if(it_1->second == 0) break;
			//If the right hand side is zero coefficient don't multiply
			if(it_2->second == 0) continue;
			int c = it_1->second * it_2->second;
			int d = it_1->first + it_2->first;
			total.insert(c,d);
		}
	}
	return total;
}

std::ostream& operator<< (std::ostream& os, const SortedLinkedList& list)
{
    if(list.getSize() == 0){
        os << "0 0 ";
    }

    for(std::map<int,int>::const_reverse_iterator it = list.canonicalForm.rbegin();
		it != list.canonicalForm.rend(); it++)
    {
		os << it->second << " " << it->first << " ";
	}
	return os;
}

std::string SortedLinkedList::printOriginal() const
{
	std::stringstream list;
    if(originalForm.size() == 0){
        list << "0 0 ";
    }

    for(std::list<node>::const_iterator it = originalForm.begin();
		it != originalForm.end(); it++)
    {
		list << it->c << " " << it->d << " ";
	}
	return list.str();
}

std::string SortedLinkedList::toString() const
{
    std::stringstream list;
    if(canonicalForm.size() == 0){
        list << "0 0 ";
    }

    for(std::map<int,int>::const_reverse_iterator it = canonicalForm.rbegin();
		it != canonicalForm.rend(); it++)
    {
		list << it->second << " " << it->first << " ";
	}
	return list.str();
}