<?php

class Extra extends BaseExtra
{
	/**
	 * Flag that shows if an Extra is mandatory for an Intent
	 * @var        boolean
	 */
	protected $mandatory = null;
	/**
	 * TODO Dcumentation
	 * @return     Mandatory Flaf
	 */
	public function isMandatory()
	{
		return $this->mandatory;
	}

	public function setMandatory($bool)
	{
		$this->mandatory = $bool;
	}
	public function __toString(){
		return $this->getName()." ".$this->getDescription().")";
	}

}
