<?php

class mashupIntentsActions extends sfActions {

	public function executeIndex(sfWebRequest $request) {
		
		$criteria = new Criteria ();
		$criteria->add(IntentPeer::MASHUP,true);
		$this->mashupIntents = IntentPeer::doSelect ($criteria );
		$this->applications = ApplicationPeer::doSelect ( new Criteria ( ) );

	}
}
