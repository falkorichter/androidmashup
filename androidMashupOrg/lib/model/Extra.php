<?php

class Extra extends BaseExtra
{

	protected $mandatory = null;
	protected $relationDescription = "";

	public function isMandatory()
	{
		return $this->mandatory;
	}

	public function setMandatory($bool)
	{
		$this->mandatory = $bool;
	}
	
	public function setRelationDescription($des)
	{
		$this->relationDescription = $des;
	}
	public function getRelationDescription()
	{
		return $this->relationDescription;
	}
	
	public function __toString(){
		return $this->getName()." (".$this->getDescription().")";
	}

}
