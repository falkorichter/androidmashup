<?php

class Extratype extends BaseExtratype
{
	public function __toString(){
		return $this->getName();
	}
}
