/*
 * Copyright (c) 2023-2024 Playlab/ACAL
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met: redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer;
 * redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution;
 * neither the name of the copyright holders nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#ifndef BLACKBEARCOMPILER_INCLUDE_BASE_HH__
#define BLACKBEARCOMPILER_INCLUDE_BASE_HH__

#include <torch/script.h>

#include <unordered_map>
#include <unordered_set>
#include <vector>

class DAG {
private:
	std::unordered_map<int, std::string>      nodeList;
	std::unordered_map<int, std::vector<int>> adjList;  // from, to

public:
	enum DAG_PREDEFINE
	{
		START_NODE,
		INPUT_MODULE,
		END_NODE
	};
	int  size() { return nodeList.size(); }

	int addNode(std::string name) {
		int node_id = size();
		addNode(node_id,name);
		return node_id;
	}
	void addNode(int val, std::string name) {
		std::cout << "add Node:" << val << "," << name << std::endl;
		if (nodeList.find(val) == nodeList.end()) { nodeList[val] = name; }
		if (adjList.find(val) == adjList.end()) { adjList[val] = {}; }
	}

	bool haveModuleName(std::string name) {
		for (auto n : nodeList) {
			if (n.second == name) return true;
		}
		return false;
	}
	int findNodeid(std::string name) {
		for (auto n : nodeList) {
			if (n.second == name) return n.first;
		}
		return -1;
	}

	void addEdge(int from, int to) {
		std::cout << "add Edge:" << from << "," << to << std::endl;
		adjList[from].push_back(to);
	}

	void dump() {
		std::cout << "node = {" << std::endl;
		for (auto n : nodeList) { printNode(n.first); }
		std::cout << "}" << std::endl;
		std::cout << "edge = { " << std::endl;
		for (auto pair : adjList) {
			std::cout << pair.first << ":";
			for (auto s : pair.second) { std::cout << s << ","; }
			std::cout << std::endl;
		}
		std::cout << "}" << std::endl;
	}

	void printNode(int node) { std::cout << node << ":\t\"" << nodeList[node] << "\"," << std::endl; }

	void dfs(int node, std::unordered_set<int>& visited) {
		if (visited.find(node) != visited.end()) { return; }
		visited.insert(node);
		printNode(node);

		for (int neighbor : adjList[node]) { dfs(neighbor, visited); }
	}

	void replaceStartPoint(int origin, int child) {
		for (auto pair : adjList) {
			for (auto s : pair.second) {
				if (s == origin) {
					addEdge(pair.first, child);
					removeEdge(pair.first, origin);
				}
			}
		}
	}

	void removeEdge(int from, int to) {
		std::cout << "remove Edge:" << from << "," << to << std::endl;
		auto it = std::remove(adjList[from].begin(), adjList[from].end(), to);
		adjList[from].erase(it, adjList[from].end());
	}

	int findEndWith(int end) {
		for (auto pair : adjList) {
			for (auto s : pair.second) {
				if (s == end) { return pair.first; }
			}
		}
		return 0;
	}

	void traverse() {
		std::unordered_set<int> visited;
		for (const auto& pair : adjList) {
			if (visited.find(pair.first) == visited.end()) { dfs(pair.first, visited); }
		}
		std::cout << std::endl;
	}

	void toGraphviz() const {
        
		std::cout<< "digraph G {\n";
        // 遍歷節點並添加到 DOT 文件
        for (const auto& node : nodeList) {
            std::cout << "    " << node.first << " [label=\"" << node.second << "\"];\n";
        }

        // 遍歷相鄰表並添加邊到 DOT 文件
        for (const auto& adj : adjList) {
            int from = adj.first;
            for (int to : adj.second) {
                std::cout << "    " << from << " -> " << to << ";\n";
            }
        }

        // 結束 DOT 語法
        std::cout << "}\n";
    }
};

class IRTracer {
public:

	enum GraphInput
	{
		self,
		x
	};

	enum CallMethodInput
	{
		object,
		input
	};


	IRTracer(std::string _name) : name(_name) {}
	~IRTracer() {}

	void loadModel(std::string modelFileName);
	void run(torch::Tensor input);

	torch::Tensor      traceModule(torch::jit::script::Module* module, torch::Tensor input);
	
	void ListParameters();
	void ListFirstAttributes();
	void getWeightMemory();
	void dumpInputOutput(torch::jit::Node* node);
	void traceModuleOrder();
protected:
	torch::Tensor      traceModule(torch::jit::script::Module* module, torch::Tensor input, int parentIR_id);

	void getConstantParam(torch::jit::Node* parent_node, std::string parme_type_string);
	void getListParam(torch::jit::Node* parent_node);
	void getGetAttrParam(torch::jit::Node* parent_node, torch::jit::named_attribute_list attribut_list);

	torch::Tensor* prepareChildInput(torch::Tensor* currentInput, torch::jit::Value* forward_input);	
	torch::jit::IValue ComputeOperatorNode(torch::jit::script::Module* module, torch::jit::Node* node,
	                                     torch::Tensor input);
	std::string searchChildModule(torch::jit::script::Module* module, std::string searchName);
	void traceModuleOrder(torch::jit::script::Module* module, int parentIR_id);

private:
	DAG                               ir_dag;

	std::string                       name;
	torch::jit::Module                module;
	torch::jit::Stack                 stack;

	std::vector<torch::jit::Operator> globle_op_list;
	std::vector<torch::Tensor>        globle_tensor_list;
	std::map<torch::jit::Value*, torch::jit::IValue> map_method_outputs; // node->output() map to output tensor
	
};

#endif  // BLACKBEARCOMPILER_INCLUDE_BASE_HH__
