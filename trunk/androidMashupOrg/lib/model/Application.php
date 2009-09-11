<?php

class Application extends BaseApplication
{
	
	/**
   * Convenience method to fetch all related Intent objects.
   * @param Criteria $c An [optional] Criteria to limit results
   * @return array Intent[]
   */
	public function getIntents($c = null) {
     $intents = array();
     foreach($this->getApplicationHasIntentsJoinIntent($c) as $ref) {
        $intents[] = $ref->getIntent();
     }
     return $intents;
  }
  public function getMashupIntents($c = null) {
     $intents = array();
     foreach($this->getApplicationHasIntentsJoinIntent($c) as $ref) {
     if($ref->getIntent()->getMashup() == 1){
        $intents[] = $ref->getIntent();
        }
     }
     return $intents;
  }
    public function getPrivateIntents($c = null) {
     $intents = array();
     foreach($this->getApplicationHasIntentsJoinIntent($c) as $ref) {
     if($ref->getIntent()->getMashup() == 0){
        $intents[] = $ref->getIntent();
        }
     }
     return $intents;
  }

  public function __toString(){
  	return $this->getName();
  }
	
}
