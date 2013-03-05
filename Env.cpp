#include "Env.h"
const PTObject* Env::operator[] (const std::string& key) const {
	return store.find(key)->second;
}
void Env::insert(const std::string& key, const PTObject* val) {
	store[key] = val;
}
