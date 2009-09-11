<?php

class Intent extends BaseIntent
{	
	/**
   * Convenience method to fetch all related Extra objects.
   * @param Criteria $c An [optional] Criteria to limit results
   * @return array Extra[]
   */
	public function getExtras($c = null) {
     $extras = array();
     foreach($this->getIntentHandlesExtrasJoinExtra($c) as $ref) {
     	$extra = $ref->getExtra();
     	$extra->setMandatory($ref->getMandatory());
        $extras[] = $extra;
     }
     return $extras;
  }
  
	
  public function __toString(){
		return $this->getTitle();
	}
}
