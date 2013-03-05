#include "Env.h"
Env::Env() : parent_env(NULL) {}

Env::Env(Env *parent) : parent_env(parent) {}

const PTObject* Env::operator[] (const std::string& key) const {
	store_t::const_iterator r = store.find(key);
	if (r != store.end()) {
		return r->second;
	}
	else if (parent_env) {
		return (*parent_env)[key];
	}
	else {
		return NULL;
	}
}
void Env::insert(const std::string& key, const PTObject* val) {
	store[key] = val;
}
