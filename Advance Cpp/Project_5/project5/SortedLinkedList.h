#ifndef SORTEDLINKEDLIST_H
#define SORTEDLINKEDLIST_H

#include "Node.h"

class SortedLinkedList
{
   private:
      Node * head;	//Dumby node
      Node * tail;
      int size;

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
	  //Push items to the last item of the list
	  void push(const int&, const int&);
	  //Reinsert the intems in canonical form
	  void validate();
      //Insert coefficient and exponent to the list
      void insert(const int&, const int&);
	  SortedLinkedList multiply(const int&, const int&);
      //Overloading operators
      SortedLinkedList operator+ (const SortedLinkedList&);
      SortedLinkedList operator- (const SortedLinkedList&);
      SortedLinkedList operator* (const SortedLinkedList&);
      bool operator== (const SortedLinkedList&);
      friend std::ostream& operator<< (std::ostream& os, const SortedLinkedList& node);
	  //Returns the contents of this list
	  std::string toString() const;
      void printTail()const{std::cout<<tail<<std::endl;}
};
#endif  /* SORTEDLINKEDLIST_H */

/* SortedLinkedList.cpp */
//Copy the entire list to this list
void SortedLinkedList::copyList(const SortedLinkedList& list)
{
    for(Node * curr = list.head->next; curr != NULL; curr = curr->next)
    {
        Node * newNode = new Node(curr->coefficient, curr->exponent);
        tail->next = newNode;
        tail = newNode;
    }
}

inline SortedLinkedList::SortedLinkedList():size(0)
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : Empty Constructor" << std::endl;
	#endif	
	head = new Node();  //Dumby node
	tail = head;
}

inline SortedLinkedList::SortedLinkedList(const SortedLinkedList& list):size(list.size)
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : Copy Constructor" << std::endl;
	#endif
	//if(this != &list){
	    head = new Node();  //Dumby node
        tail = head;
	    //Copy the list
	    copyList(list);
	//}
}

inline SortedLinkedList& SortedLinkedList::operator= (const SortedLinkedList& list)
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

inline SortedLinkedList::~SortedLinkedList()
{
	#ifdef DEBUG
		std::cout << "SortedLinkedList : ~Destructor" << std::endl;
		//std::cout << (*this) << std::endl;
		std::cout << "-------------" << std::endl;
	#endif
	clear();
	delete head;
}

inline int SortedLinkedList::getSize() const
{
   return size;
}

inline bool SortedLinkedList::isEmpty() const
{
   return size == 0;
}

void SortedLinkedList::clear()
{
    #ifdef DEBUG
    std::cout << "Size : " << size << std::endl;
    #endif

    Node * current = head->next;
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

//Append items to the tails
void SortedLinkedList::push(const int& coefficient, const int& exponent)
{
	Node * newNode = new Node(coefficient, exponent);	
	tail->next = newNode;
	tail = newNode;
	size++;
}

void SortedLinkedList::validate()
{
	//Make a copy of the list
	SortedLinkedList temp(*this);
	//Clear the current list
	clear();
	//Insert only valid data
	for(Node * current = temp.head->next; current != NULL; current = current->next)
	{
		insert(current->coefficient, current->exponent);
	}
}

void SortedLinkedList::insert(const int& coefficient, const int& exponent)
{
    //Don't add 0 coefficients
    if(coefficient == 0) return;

    Node * newNode;
    Node * prev = head;
    Node * currentNode = head->next;

    //If the list is empty, insert the first node
    if(currentNode == NULL)
    {
        newNode = new Node(coefficient,exponent);
        head->next = newNode;
        tail = newNode;
        size++;
    }
    else
    {
        while(currentNode != NULL)
        {
            //If this degree is higher than the current, the preppend
            if(exponent > currentNode->exponent)
            {
                newNode = new Node(coefficient,exponent);
                prev->next = newNode;
                newNode->next = currentNode;

                if(currentNode->next == NULL){
                    tail = currentNode;
                }
                size++;
                break;
            }
            //If they have the same exponent, do arithmetic operations on coefficient before adding
            else if(exponent == currentNode->exponent)
            {
                //If their coefficient adds up to 0, remove it
                if((coefficient + currentNode->coefficient) == 0)
                {
                    prev->next = currentNode->next;
                    delete currentNode;       //free space of 0 coefficient node
                    //Reassign tail
                    if(prev->next == NULL)
                    {
                        tail = prev;
                    }
                    size--;
                }
                else
                {
                    currentNode->coefficient += coefficient;
                }
                break;
            }
            //The node is smaller
            else
            {
                prev = prev->next; //prev = current;
                currentNode = currentNode->next;

                //If this is the smallest degree append last
                if(currentNode == NULL)
                {
                    newNode = new Node(coefficient,exponent);
                    prev->next = newNode;
                    tail = newNode;
                    size++;
                    break; //Don't need to break bcuz for the next iteration it will evaluate to false
                }
            }
        }
    }
}

SortedLinkedList SortedLinkedList::multiply (const int& coefficient, const int& exponent)
{
    Node * currentNode = head->next;
	SortedLinkedList list;

    //If the list is empty or multiplier is zero then return an empty list
    if(currentNode != NULL && coefficient != 0)
    {
        while(currentNode != NULL)
        {
			//Multiply coefficients and add exponents
            list.insert(currentNode->coefficient * coefficient,
						currentNode->exponent + exponent);
			
			//Go to the next node
			currentNode = currentNode->next;					
        }
    }
	return list;
}

SortedLinkedList SortedLinkedList::operator+ (const SortedLinkedList& list)
{
	SortedLinkedList temp(*this);
	for(Node * current = list.head->next; current != NULL; current = current->next)
	{
		temp.insert(current->coefficient, current->exponent);
	}
	return temp;
}

SortedLinkedList SortedLinkedList::operator- (const SortedLinkedList& list)
{
	SortedLinkedList temp(*this);
	for(Node * current = list.head->next; current != NULL; current = current->next)
	{
		temp.insert( -1 * current->coefficient, current->exponent);
	}
	return temp;
}

SortedLinkedList SortedLinkedList::operator* (const SortedLinkedList& list)
{
	SortedLinkedList total;
	for(Node * currentLH = list.head->next; currentLH != NULL; currentLH = currentLH->next)
	{
		for(Node * currentRH = head->next; currentRH != NULL; currentRH = currentRH->next)
		{
			//None of the list have zero coefficients
			//It's taken cared of in the insert method
			//If the left hand side is zero coefficient don't loop
			if(currentLH->coefficient == 0) break;
			//If the right hand side is zero coefficient don't multiply
			if(currentRH->coefficient == 0) continue;
			
			total.insert(currentRH->coefficient * currentLH->coefficient,
						 currentRH->exponent + currentLH->exponent);
		}
	}
	return total;
}

bool SortedLinkedList::operator== (const SortedLinkedList& list)
{
	//If both are the same pointer then it's definetely true
	if(this == &list) return true;
	//If they don't have the same size then it's false
	if(size != list.size) return false;
	
	//Check that both have the same contents to be considered equal
	for(Node * LHS = head->next, * RHS = list.head->next;
		LHS != NULL && RHS != NULL;
		LHS = LHS->next, RHS = RHS->next)
	{
		if(LHS->coefficient != RHS->coefficient || LHS->exponent != RHS->exponent)
		{
			return false;
		}
	}
	return true;
}

std::ostream& operator<< (std::ostream& os, const SortedLinkedList& list)
{
	Node * current = list.head->next;
	//If this polynomial is empty or head == tail
    if(list.head == list.tail){
        os << "0 0 ";
    }

    for(; current != NULL; current = current->next)
    {
		os << *current;
	}
	return os;
}

std::string SortedLinkedList::toString() const
{
    std::stringstream list;
    Node * current = head->next;
    //If this polynomial is empty or head == tail
    if(head == tail){
        list << "0 0 ";
    }

    for(; current != NULL; current = current->next)
    {
		list << current->toString();
	}
	return list.str();
}