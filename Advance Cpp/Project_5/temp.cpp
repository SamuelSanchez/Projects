#include <iostream>
using namespace std;

class A{
	public:
		int a;
		A(){
			a = 10;
			cout << " a : " << a << endl;
		}
		virtual ~A(){
			cout << " A's dtor " << endl;
		}
		void x(){
			cout << " A's x" << endl;
		}
};
class B: public A{
	public:
		int b;
		B(){
			b = 20;
			cout << " b : " << b << endl;
		}
		~B(){
			cout << " B's dtor " << endl;
		}
		void x(){
			cout << " B's x " << endl;
		}
};
class C: public A{
	public:
		int c;
		C(){
			c = 30;
			cout << " c : " << c << endl;
		}
		~C(){
			cout << " C's dtor " << endl;
		}
		void x(){
			cout << " C's x " << endl;
		}
};
class D: public B, public C{
	public:
		int d;
		D(){
			d = 40;
			cout << " d : " << d << endl;
		}
		~D(){
			cout << " D's dtor " << endl;
		}
		void x(){
			cout << " D's x " << endl;
		}
};
int main(){
	//D d;
	//d.x();
	A * a = new B();
	delete a;
}