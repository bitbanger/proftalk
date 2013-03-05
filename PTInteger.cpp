#include "PTInteger.h"
PTInteger::PTInteger(int value) : value(value) {}
const PTObject* PTInteger::eval(const Env *env) const {
	return this; //can't do anything to help you here
}
