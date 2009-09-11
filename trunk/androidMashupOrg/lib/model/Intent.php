<?php

class Intent extends BaseIntent
{	

	public function getExtras($c = null) {
     $extras = array();
     foreach($this->getIntentHandlesExtrasJoinExtra($c) as $ref) {
     	$extra = $ref->getExtra();
     	$extra->setMandatory($ref->getMandatory());
		$extra->setRelationDescription($ref->getDescription());
        $extras[] = $extra;
     }
     return $extras;
  }
  
	
  public function __toString(){
		return $this->getTitle();
	}
}
