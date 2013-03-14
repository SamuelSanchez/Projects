#ifndef PERMUTEDINDEX
#define PERMUTEDINDEX_H

class PermutedIndex
{
   private:
      std::map<std::string,std::map<int,int> > dictionary;
	  //class test{ ~test(){std::cout << "DTOR" << std::endl; };
	  //test a;
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
	if( dictionary.count( word ) > 0 ){
		std::map<std::string,std::map<int,int> >::iterator it = dictionary.find( word );
		
		//Check if the word belongs to an existing line
		if( it->second.count( line ) > 0 ){
			std::map<int,int>::iterator ele = it->second.find(line);
			ele->second = ele->second + 1;
		}//New line
		else{
			it->second.insert( std::pair<int,int>( line, 1 ) );	
		}
	}
	else{//Insert the new workd with the line just found
		std::map<int,int> counter;
		counter.insert( std::pair<int,int>( line, 1 ) );
		dictionary.insert( std::pair<std::string,std::map<int,int> >( word, counter ) );
	}
}

std::ostream& operator<< (std::ostream& os, const PermutedIndex& obj)
{
	int mapSize = 0;
    for(std::map<std::string, std::map<int,int> >::const_iterator it = obj.dictionary.begin();
		it != obj.dictionary.end(); it++)
    {
		os << it->first << ":";
		mapSize = it->second.size();	//Get size of the map
		int index = 1; 					//Start at one
		for(std::map<int,int>::const_iterator ele = it->second.begin();
			ele != it->second.end(); ele++)
		{
			os << " " << ele->first;
			//If there are more elements in the same line then display
			if(ele->second > 1){
				os << "(" << ele->second << ")";
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