<?php

/**
 * Extratype form base class.
 *
 * @package    mashup
 * @subpackage form
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseExtratypeForm extends BaseFormPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'id_extraType' => new sfWidgetFormInputHidden(),
      'name'         => new sfWidgetFormInput(),
      'description'  => new sfWidgetFormInput(),
    ));

    $this->setValidators(array(
      'id_extraType' => new sfValidatorPropelChoice(array('model' => 'Extratype', 'column' => 'id_extraType', 'required' => false)),
      'name'         => new sfValidatorString(array('max_length' => 45, 'required' => false)),
      'description'  => new sfValidatorString(array('max_length' => 45, 'required' => false)),
    ));

    $this->widgetSchema->setNameFormat('extratype[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'Extratype';
  }


}
