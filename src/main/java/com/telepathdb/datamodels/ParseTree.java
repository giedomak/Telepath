package com.telepathdb.datamodels;

/**
 * Created by giedomak on 20/01/2017.
 */
public class ParseTree {

  private String payload;
  public ParseTree left;
  public ParseTree right;
  private boolean hasDirectOpeningGate;
  private boolean hasInverseOpeningGate;
  private String id;

//  private Coster coster;
//  private CostTable ctable;
//  private RelationSet relset;
//  private Provenance prov;

  public static String DIRECT_OPEN_GATE = "└";
  public static String DIRECT_CLOSE_GATE = "┐";
  public static String INVERSE_OPEN_GATE = "┘";
  public static String INVERSE_CLOSE_GATE = "┌";


  public ParseTree getLeft() {
    return left;
  }

  public ParseTree getRight() {
    return right;
  }

  public ParseTree() {
    this.payload = "";
    this.left = null;
    this.right = null;
//    this.coster = null;
//    this.prov = null;
  }

  public ParseTree(int id) {
    this.id = Integer.toString(id);
    this.payload = "";
    this.left = null;
    this.right = null;
//    this.coster = null;
//    this.prov = null;
  }
//
//  public Pair<Relation,Relation> getJoinPredicate() {
//
//    RelationSet leftRelSet;
//    RelationSet rightRelSet;
//
//    if(this.getLeft().getRelSet()==null || this.getRight().getRelSet()==null) {
//      // no relsets attached to children
//      // need to generate the relsets and check the graph
//      leftRelSet = new RelationSet(this.getLeft());
//      rightRelSet = new RelationSet(this.getRight());
//    } else {
//      leftRelSet = this.getLeft().getRelSet();
//      rightRelSet = this.getRight().getRelSet();
//    }
//
//    QueryConcats qg = this.coster.getEstimator().getQueryGraph();
//
//    if(qg != null) {
//      return qg.getJoinPredicate(leftRelSet, rightRelSet);
//    } else {
//      return null;
//    }
//  }
//
//  public void setRelSet(RelationSet rs) {
//    this.relset = rs;
//  }
//
//  public RelationSet getRelSet() {
//    return relset;
//  }
//
//  public void setCoster(Coster c) {
//    this.coster = c;
//  }
//
//  public void setCostTable(CostTable ct) {
//    this.ctable = ct;
//  }
//
//  public void propagateCoster(Coster c) {
//    this.coster = c;
//    if(this.left != null) this.left.propagateCoster(c);
//    if(this.right != null) this.right.propagateCoster(c);
//  }
//
//  public void propagateProvenance(Provenance p) {
//    this.prov = p;
//    if(this.left != null) this.left.propagateProvenance(p);
//    if(this.right != null) this.right.propagateProvenance(p);
//  }
//
//  public boolean isDirectClosingGate() {
//    if(this.relset != null) return relset.isDirectClosingGate();
//    return this.payload.equals(TreePlan.DIRECT_CLOSE_GATE);
//  }
//
//  public boolean isInverseClosingGate() {
//    if(this.relset != null) return relset.isInverseClosingGate();
//    return this.payload.equals(TreePlan.INVERSE_CLOSE_GATE);
//  }
//
//  public boolean isGate() {
//    return isInverseClosingGate() || isInverseOpeningGate() || isDirectClosingGate() || isDirectOpeningGate();
//  }
//
//  public boolean hasDirectOpeningGate() {
//    if(this.relset != null) return relset.hasDirectOpeningGate();
//    if(isDirectOpeningGate()) return true;
//    return this.hasDirectOpeningGate;
//  }
//
//  public boolean hasInverseOpeningGate() {
//    if(this.relset != null) return relset.hasInverseOpeningGate();
//    if(isInverseOpeningGate()) return true;
//    return this.hasInverseOpeningGate;
//  }
//
//  public boolean isDirectOpeningGate() {
//    if(this.relset != null) return relset.isDirectOpeningGate();
//    return this.payload.equals(TreePlan.DIRECT_OPEN_GATE);
//  }
//
//  public boolean isInverseOpeningGate() {
//    if(this.relset != null) return relset.isInverseOpeningGate();
//    return this.payload.equals(TreePlan.INVERSE_OPEN_GATE);
//  }
//
//  public void setHasDirectOpeningGate(boolean flag) {
//    this.hasDirectOpeningGate = flag;
//  }
//
//  public void setHasInverseOpeningGate(boolean flag) {
//    this.hasInverseOpeningGate = flag;
//  }
//
//  public TreePlan(String payload, TreePlan left, TreePlan right) {
//    this.payload = payload;
//    this.left = left;
//    this.right = right;
//    this.hasDirectOpeningGate = false;
//    this.hasInverseOpeningGate = false;
//  }
//
//  public TreePlan(String payload) {
//    this.payload = payload;
//    this.left = null;
//    this.right = null;
//    this.hasDirectOpeningGate = false;
//    this.hasInverseOpeningGate = false;
//  }
//
//  public static LinkedList<TreePlan> parseFromPropertyPath(String pp) {
//
//    ANTLRInputStream input = new ANTLRInputStream(pp); // create a lexer that feeds off of input CharStream
//    PropertyPathLexer lexer = new PropertyPathLexer(input); // create a buffer of tokens pulled from the lexer
//    CommonTokenStream tokens = new CommonTokenStream(lexer); // create a parser that feeds off the tokens buffer
//    PropertyPathParser parser = new PropertyPathParser(tokens);
//    ParseTree tree = parser.expr(); // begin parsing at expr rule
//
//    TreePlanVisitorWithGates visitor = new TreePlanVisitorWithGates();
//    LinkedList<TreePlan> tpn = visitor.visit(tree);
//
//    return tpn;
//  }
//
//  public static TreePlan parseFromPropertyPathNoGates(String pp) {
//
//    ANTLRInputStream input = new ANTLRInputStream(pp); // create a lexer that feeds off of input CharStream
//    PropertyPathLexer lexer = new PropertyPathLexer(input); // create a buffer of tokens pulled from the lexer
//    CommonTokenStream tokens = new CommonTokenStream(lexer); // create a parser that feeds off the tokens buffer
//    PropertyPathParser parser = new PropertyPathParser(tokens);
//    ParseTree tree = parser.expr(); // begin parsing at expr rule
//
//    TreePlanVisitorNoGates visitor = new TreePlanVisitorNoGates();
//    TreePlan tpn = visitor.visit(tree);
//
//    return tpn;
//  }
//
//  public TreePlan addInverseGates() {
//
//    // assuming the tree is right-associative -- TODO: is this check required??
//    TreePlan thisCopy = this.deepCopy();
//
//    // not adding gates to atom nodes
//    TreePlan out = thisCopy;
//
//    // add inverse opening gate
//    // drill right
//    TreePlan parent = thisCopy;
//    TreePlan right = thisCopy.getRight();
//    if(right != null) {
//
//      while(!right.isLeaf()) {
//        parent = right;
//        // need to update all the plans on left-deep that they have
//        // an opening gate
//        right.setHasInverseOpeningGate(true);
//        right = right.getLeft();
//      }
//
//      // we are now at rightmost leaf
//      TreePlan newRight = new TreePlan(TreePlan.INVERSE_OPEN_GATE);
//      newRight.setHasInverseOpeningGate(true);
//      TreePlan newLeft = new TreePlan(right.getPayload());
//
//      // replace the old leaf (has opening gate)
//      TreePlan foo = new TreePlan("/", newLeft, newRight);
//      foo.setHasInverseOpeningGate(true);
//      parent.addRight(foo);
//
//      // add opening gates flags
//      thisCopy.setHasInverseOpeningGate(true);
//
//      // add direct closing gate
//      out = new TreePlan("/", new TreePlan(TreePlan.INVERSE_CLOSE_GATE), thisCopy);
//      out.setHasInverseOpeningGate(true); // will also have opening gate
//    }
//
//    // convert to "almost" left deep
//    // needed to process all parenthisations!
//    out.addRight(out.getRight().makeLeftDeep());
//
//    return out;
//
//  }
//
//
//  public TreePlan addDirectGates() {
//
//    // assuming the tree is left-associative -- TODO: is this check required??
//    TreePlan thisCopy = this.deepCopy();
//
//    // not adding gates to atom nodes
//    TreePlan out = thisCopy;
//
//    // add [ gate
//    // drill left
//    TreePlan parent = thisCopy;
//    TreePlan left = thisCopy.getLeft();
//    if(left != null) {
//
//      while(!left.isLeaf()) {
//        parent = left;
//        // need to update all the plans on left-deep that they have
//        // an opening gate
//        left.setHasDirectOpeningGate(true);
//        left = left.getLeft();
//      }
//
//      // we are now at leftmost leaf
//      TreePlan newLeft = new TreePlan(TreePlan.DIRECT_OPEN_GATE);
//      newLeft.setHasDirectOpeningGate(true);
//      TreePlan newRight = new TreePlan(left.getPayload());
//
//      // replace the old leaf (has opening gate)
//      TreePlan foo = new TreePlan("/", newLeft, newRight);
//      foo.setHasDirectOpeningGate(true);
//      parent.addLeft(foo);
//
//      // add opening gates flags
//      thisCopy.setHasDirectOpeningGate(true);
//
//      // add direct closing gate
//      out = new TreePlan("/", thisCopy, new TreePlan(TreePlan.DIRECT_CLOSE_GATE));
//      out.setHasDirectOpeningGate(true); // will also have opening gate
//    }
//
//    return out;
//
//  }
//
//  public boolean isKleenePlus() {
//    return this.payload.equals("+");
//  }
//
//  public boolean isKleeneStar() {
//    return this.payload.equals("*");
//  }
//
//  public boolean isConcat() {
//    return this.payload.equals("/");
//  }
//
//  public void addRight(TreePlan tpn) {
//    this.setHasInverseOpeningGate(tpn.hasInverseOpeningGate());
//    right = tpn;
//  }
//
//  public void addLeft(TreePlan tpn) {
//    this.setHasDirectOpeningGate(tpn.hasDirectOpeningGate());
//    left = tpn;
//  }
//
//  public void setPayload(String sop) {
//    this.payload = sop;
//  }
//
//  public String getPayload() {
//    return this.payload;
//  }
//
//  public boolean isLeaf() {
//    return (this.left == null && this.right == null);
//  }
//
//  public boolean isUnary() {
//    return (this.left != null && this.right == null);
//  }
//
//  public boolean isBinary() {
//    return (!this.isUnary() && !this.isLeaf());
//  }
//
//  public String toString() {
//    if(isLeaf()) {
//      return payload;
//    } else if(isUnary()) {
//      return "(" + left.toString() + ")" + payload;
//    } else {
//      // binary op
//      return "(" + left.toString() + payload + right.toString() + ")";
//    }
//
//  }
//
//  public void setProvenance(Provenance p) {
//    this.prov = p;
//  }
//
//  public TreePlan deepCopy() {
//
//    TreePlan tpn = new TreePlan();
//    tpn.setPayload(this.payload);
//    tpn.setCoster(this.coster);
//    tpn.setProvenance(this.prov);
//    tpn.setHasDirectOpeningGate(this.hasDirectOpeningGate());
//    tpn.setHasInverseOpeningGate(this.hasInverseOpeningGate());
//    tpn.setId(this.id);
//    if(this.left != null) tpn.addLeft(this.left.deepCopy());
//    if(this.right != null) tpn.addRight(this.right.deepCopy());
//
//    return tpn;
//  }
//
//
//
//  public TreePlan makeLeftDeep() {
//
//    TreePlan out = this.deepCopy();
//
//    while(associateLeft(out) != null) {
//      out = associateLeft(out);
//    }
//
//    return out;
//
//  }
//  public LinkedList<TreePlan> getParenthisations() {
//
//    LinkedList<TreePlan> treeList = new LinkedList<TreePlan>();
//
//    // binary operator
//    if(isBinary()) {
//      LinkedList<TreePlan> leftParens = left.getParenthisations();
//      LinkedList<TreePlan> rightParens = right.getParenthisations();
//      for(TreePlan leftTree : leftParens) {
//        for(TreePlan rightTree : rightParens) {
//
//          TreePlan tpn = new TreePlan(this.payload, leftTree.deepCopy(), rightTree.deepCopy());
//
//          // propagate opening gates flags
//          tpn.setHasDirectOpeningGate(leftTree.hasDirectOpeningGate());
//          tpn.setHasInverseOpeningGate(rightTree.hasInverseOpeningGate());
//
//          // rewind to left
//          // and then consider all parenthisations
//          tpn = tpn.deepCopy();
//
//          while (associateLeft(tpn) != null) {
//            tpn = associateLeft(tpn);
//          }
//
//          while(tpn != null) {
//            treeList.add(tpn);
//            tpn = associateRight(tpn);
//          }
//
//        }
//
//      }
//    }
//
//    // unary operator
//    if(isUnary()) {
//      for(TreePlan leftTree : left.getParenthisations()) {
//        TreePlan tpn = new TreePlan(this.payload);
//        tpn.addLeft(leftTree.deepCopy());
//        treeList.add(tpn);
//      }
//    }
//
//    // leaf node
//    if(isLeaf()) treeList.add(this);
//
//    // re-enumerate the tree plans
//
//    return treeList;
//  }
//
//  TreePlan associateLeft(TreePlan root) {
//
//    TreePlan newRoot = null;
//
//    // adding a restriction for inverse closing gates
//    if(root.getRight().isBinary() && !root.getLeft().isInverseClosingGate()) {
//      newRoot = new TreePlan(this.payload);
//
//      newRoot.addRight(root.getRight().getRight().deepCopy());
//
//      TreePlan newLeft = new TreePlan(this.payload);
//      newLeft.setHasDirectOpeningGate(root.getLeft().hasDirectOpeningGate());
//      newLeft.addRight(root.getRight().getLeft().deepCopy());
//      newLeft.addLeft(root.getLeft().deepCopy());
//
//      newRoot.addLeft(newLeft);
//    }
//
//    return newRoot;
//  }
//
//  TreePlan associateRight(TreePlan root) {
//
//    TreePlan newRoot = null;
//
//    // adding a restriction for direct closing gates
//    if(root.getLeft().isBinary() && !root.getRight().isDirectClosingGate()) {
//      newRoot = new TreePlan(this.payload);
//
//      newRoot.addLeft(root.getLeft().getLeft().deepCopy());
//
//      TreePlan newRight = new TreePlan(this.payload);
//      newRight.setHasInverseOpeningGate(root.getRight().hasInverseOpeningGate());
//      newRight.addLeft(root.getLeft().getRight().deepCopy());
//      newRight.addRight(root.getRight().deepCopy());
//
//      newRoot.addRight(newRight);
//    }
//
//    return newRoot;
//  }
//
//  // wrapper for debugging
//  class PlanList {
//
//    private LinkedList<WavePlan> planList;
//
//    public PlanList() {
//      planList = new LinkedList<>();
//    }
//
//    public void add(WavePlan w) {
//
//      // data breakpoint
//      if(w.toString().equals("digraph 1 {\n" +
//          ">0 - 1 [label=\"+<isConnectedTo>\"]\n" +
//          "1 - 2 [label=\"<isLocatedIn>+\"]\n" +
//          ">3 - 4 [label=\"2+\"]\n" +
//          ">5 - 6 [label=\"<owns>+\"]\n" +
//          "*7 - 6 [label=\"<owns>+\"]\n" +
//          "6 - *7 [label=\"+4\"]\n" +
//          "}\n\n")) {
//        int br = 0;
//      }
//
//      planList.add(w);
//    }
//
//    public LinkedList<WavePlan> getPlans() {
//      return planList;
//    }
//  }
//
//  public LinkedList<EnumRule> getEnumRules() {
//
//    LinkedList<EnumRule> l = new LinkedList<>();
//
//    // which rules do we want to use?
//    l.add(new EnumRuleCC());
//    l.add(new EnumRuleCCF());
//    l.add(new EnumRuleCP());
//    l.add(new EnumRuleADC());
//    l.add(new EnumRuleCPF());
//    l.add(new EnumRuleADO());
//    l.add(new EnumRuleAA());
//    l.add(new EnumRuleAP());
//    l.add(new EnumRuleADP());
//    l.add(new EnumRuleAIO());
//    l.add(new EnumRuleAIC());
//    l.add(new EnumRuleAIP());
//    l.add(new Trans1());
//    l.add(new Pipe3());
//
//    // stub rule for leafs
//    l.add(new Gate());
//
//    if(this.coster != null) EnumRule.attachCoster(coster, l);
//    if(this.ctable != null) EnumRule.attachCostTable(ctable, l);
//    if(this.prov != null) EnumRule.attachProvenance(prov, l);
//
//    return l;
//  }
//
//  //public LinkedList<WavePlan> getWavePlansMemoized(
//
//  public LinkedList<WavePlan> getWavePlans() {
//
//    // which rules do we want to use?
//    LinkedList<EnumRule> rules = getEnumRules();
//
//    // evaluate rules one-by-one
//    PlanList planList = new PlanList();
//
//    for(EnumRule r : rules) {
//      planList.getPlans().addAll(r.evalRule(this));
//    }
//
//    // re-enumerate the plans
////        int i = 0;
////        for(WavePlan p : planList.getPlans()) {
////            p.setId(Integer.toString(i));
////            i++;
////        }
//
//    return planList.getPlans();
//
//  }
//
//  public String getId() {
//    return id;
//  }
//
//  public void setId(String id) {
//    this.id = id;
//  }
//
//  @Override
//  public boolean equals(Object other) {
//    TreePlan o = (TreePlan) other;
//    return this.toString().equals(o.toString());
//  }
}
